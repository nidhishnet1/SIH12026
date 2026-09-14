package com.example.demosih11.ui.recyclers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.demosih11.R
import com.example.demosih11.databinding.FragmentRecyclerProfileBinding

class RecyclerProfileFragment : Fragment() {

    private var _binding: FragmentRecyclerProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecyclerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackToRecyclerDash.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnRecyclerLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Recycler session cleared", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}