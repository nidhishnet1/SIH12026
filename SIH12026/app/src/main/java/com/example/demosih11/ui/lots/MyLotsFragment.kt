package com.example.demosih11.ui.lots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.demosih11.R
import com.example.demosih11.databinding.FragmentMyLotsBinding
import com.example.demosih11.db.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyLotsFragment : Fragment() {
    private var _binding: FragmentMyLotsBinding? = null
    private val binding get() = _binding!!
    private val adapter = LotsAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyLotsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.lotsRecyclerView.adapter = adapter
        
        binding.fabAddLot.setOnClickListener {
            findNavController().navigate(R.id.createLotFragment)
        }

        lifecycleScope.launch {
            AppDatabase.getDatabase(requireContext()).lotDao().getAllLots().collectLatest {
                adapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}