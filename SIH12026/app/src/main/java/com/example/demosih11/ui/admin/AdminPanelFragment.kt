package com.example.demosih11.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demosih11.databinding.FragmentAdminPanelBinding
import com.example.demosih11.model.RegistrationCache
import com.google.firebase.firestore.FirebaseFirestore

class AdminPanelFragment : Fragment() {

    private var _binding: FragmentAdminPanelBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminPanelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show only partner verification and profile information
        binding.rvPendingRegistrations.layoutManager = LinearLayoutManager(requireContext())
        db.collection("registrations")
            .get()
            .addOnSuccessListener { result ->
                val registrations = result.documents.mapNotNull { document ->
                    document.toObject(RegistrationCache.UserRegistration::class.java)
                }.toMutableList()

                binding.rvPendingRegistrations.adapter = PendingRegsAdapter(registrations)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to load registrations: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

        binding.btnLogoutAdmin.setOnClickListener {
            Toast.makeText(requireContext(), "Admin session terminated safely", Toast.LENGTH_SHORT).show()
            
            // Exit back to the isolated exclusive Admin Login Activity ecosystem completely
            val intent = Intent(requireContext(), AdminLoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}