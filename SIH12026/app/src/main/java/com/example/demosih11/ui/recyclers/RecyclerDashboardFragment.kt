package com.example.demosih11.ui.recyclers

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demosih11.R
import com.example.demosih11.databinding.FragmentRecyclerDashboardBinding
import com.google.firebase.firestore.FirebaseFirestore

class RecyclerDashboardFragment : Fragment() {

    private var _binding: FragmentRecyclerDashboardBinding? = null
    private val binding get() = _binding!!
    
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: RecyclerLotsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecyclerDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnViewEnterpriseProfile.setOnClickListener {
            findNavController().navigate(R.id.recyclerProfileFragment)
        }

        binding.btnUpdateRequirement.setOnClickListener {
            showSetPriceDialog()
        }

        // Initialize RecyclerView
        binding.rvIncomingLots.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecyclerLotsAdapter(emptyList()) { doc ->
            Toast.makeText(requireContext(), "Lot accepted! Opening dispatch channel...", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.recyclerChatFragment)
        }
        binding.rvIncomingLots.adapter = adapter

        fetchLots()

        binding.btnLogoutRecycler.setOnClickListener {
            Toast.makeText(requireContext(), "Recycler enterprise session cleared", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun fetchLots() {
        db.collection("lots")
            .whereEqualTo("status", "PENDING")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(requireContext(), "Failed to load lots", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    adapter.updateList(snapshot.documents)
                }
            }
    }

    private fun showSetPriceDialog() {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val editPrice = EditText(context).apply {
            hint = "Price per Kg (₹)"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        
        val editTarget = EditText(context).apply {
            hint = "Target Quantity (Kg)"
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        layout.addView(editPrice)
        layout.addView(editTarget)

        AlertDialog.Builder(context)
            .setTitle("Set Government Unit Pricing")
            .setMessage("Define the unit price you are offering for bulk procurement.")
            .setView(layout)
            .setPositiveButton("Publish Rates") { _, _ ->
                val price = editPrice.text.toString()
                Toast.makeText(context, "New Price Set: ₹$price/Kg", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
