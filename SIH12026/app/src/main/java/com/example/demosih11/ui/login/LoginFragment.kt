package com.example.demosih11.ui.login

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.demosih11.MainActivity
import com.example.demosih11.R
import com.example.demosih11.databinding.FragmentLoginBinding
import com.example.demosih11.ui.admin.AdminLoginActivity
import com.example.demosih11.util.LocationHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var selectedRoleIndex = 0 // 0: Kabadi Wala, 1: Recycler, 2: Admin

    private val requestLocationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            performLoginCheck()
        } else {
            Toast.makeText(requireContext(), "Location access is mandatory for security verification", Toast.LENGTH_LONG).show()
        }
    }
    private val adminLoginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val launchMode = result.data?.getStringExtra("LAUNCH_MODE")

                if (launchMode == "ADMIN_PANEL") {
                    findNavController().navigate(R.id.adminPanelFragment)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide bottom navigation upon entering login view
        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility = View.GONE

        // Listen for role toggle selection changes
        binding.toggleGroupRole.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnRoleKabadi -> {
                        selectedRoleIndex = 0
                        binding.textFormTitle.text = "Kabadi Wala Authentication"
                        binding.layoutUsername.hint = "Registered Email or Phone No"
                    }
                    R.id.btnRoleRecycler -> {
                        selectedRoleIndex = 1
                        binding.textFormTitle.text = "Recycler Portal Login"
                        binding.layoutUsername.hint = "Registered Email or Phone No"
                    }
                }
            }
        }

        binding.textGotoRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        binding.textForgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Password reset link sent to your registered contact.", Toast.LENGTH_LONG).show()
        }

        // Visible text path to launch the separate admin login activity
        binding.btnAdminPath.setOnClickListener {
            val intent = Intent(requireContext(), AdminLoginActivity::class.java)
            adminLoginLauncher.launch(intent)
        }

        binding.btnLogin.setOnClickListener {
            val usernameOrContact = binding.editUsername.text.toString().trim()
            val password = binding.editPassword.text.toString()

            if (usernameOrContact.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter authentication credentials", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check location before login
            requestLocationPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    private fun performLoginCheck() {
        val usernameOrContact = binding.editUsername.text.toString().trim()
        val password = binding.editPassword.text.toString()

        Toast.makeText(requireContext(), "Verifying secure location...", Toast.LENGTH_SHORT).show()
        
        LocationHelper.checkLocationInIndia(requireContext()) { isInIndia ->
            if (isInIndia) {
                auth.signInWithEmailAndPassword(usernameOrContact, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = auth.currentUser?.uid
                            if (uid != null) {
                                db.collection("registrations").document(uid).get()
                                    .addOnSuccessListener { document ->
                                        if (document.exists() && document.getBoolean("isVerified") == true) {
                                            when (selectedRoleIndex) {
                                                0 -> {
                                                    Toast.makeText(requireContext(), "Welcome Kabadi Wala: $usernameOrContact", Toast.LENGTH_SHORT).show()
                                                    activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)?.visibility = View.VISIBLE
                                                    findNavController().navigate(R.id.dashboardFragment)
                                                }
                                                1 -> {
                                                    Toast.makeText(requireContext(), "Authorized Recycler Session Started: $usernameOrContact", Toast.LENGTH_SHORT).show()
                                                    findNavController().navigate(R.id.recyclerDashboardFragment)
                                                }
                                            }
                                        } else {
                                            auth.signOut()
                                            Toast.makeText(requireContext(), "Login failed: Your account is pending admin approval.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                    .addOnFailureListener {
                                        auth.signOut()
                                        Toast.makeText(requireContext(), "Login failed: Could not verify account status.", Toast.LENGTH_LONG).show()
                                    }
                            } else {
                                Toast.makeText(requireContext(), "Login failed: User ID not found", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Access Denied: Service only available within India territory", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
