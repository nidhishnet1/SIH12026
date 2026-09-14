package com.example.demosih11.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.demosih11.R
import com.example.demosih11.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserProfile()

        binding.btnProfileLogout.setOnClickListener {
            auth.signOut()

            Toast.makeText(
                requireContext(),
                "Session closed. Returning to portal home.",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun loadUserProfile() {

        val uid = auth.currentUser?.uid

        if (uid == null) {
            Toast.makeText(
                requireContext(),
                "User session not found.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        db.collection("registrations")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val firstName = document.getString("firstName") ?: ""
                    val lastName = document.getString("lastName") ?: ""
                    val phone = document.getString("phone") ?: "Not provided"
                    val email = document.getString("email") ?: "Not provided"
                    val gst = document.getString("gstNo") ?: "Not provided"
                    val role = document.getString("role") ?: "User"
                    val isVerified = document.getBoolean("isVerified") ?: false

                    // Name
                    binding.textProfileName.text =
                        "$firstName $lastName".trim()

                    // Phone
                    binding.textProfilePhone.text = phone

                    // Email
                    binding.textProfileEmail.text = email

                    // GST
                    binding.textProfileGst.text = gst

                    // Role + verification status
                    binding.textProfileBadge.text =
                        if (isVerified) {
                            "VERIFIED PARTNER (${role.uppercase()})"
                        } else {
                            "PENDING VERIFICATION • ${role.uppercase()}"
                        }

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Profile data not found.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Unable to load profile: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}