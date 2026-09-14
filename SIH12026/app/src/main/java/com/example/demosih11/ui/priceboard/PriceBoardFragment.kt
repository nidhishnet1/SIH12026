package com.example.demosih11.ui.priceboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demosih11.R
import com.example.demosih11.databinding.FragmentPriceBoardBinding
import com.example.demosih11.model.MaterialPrice

class PriceBoardFragment : Fragment() {
    private var _binding: FragmentPriceBoardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPriceBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val prices = listOf(
            MaterialPrice(getString(R.string.cat_pcb), "₹ 350/kg", "↑ Increasing", R.drawable.ic_lots),
            MaterialPrice(getString(R.string.cat_battery), "₹ 85/kg", "→ Stable", R.drawable.ic_safety),
            MaterialPrice(getString(R.string.cat_crt), "₹ 200/pc", "↓ Decreasing", R.drawable.ic_home),
            MaterialPrice(getString(R.string.cat_cable), "₹ 450/kg", "↑ High Demand", R.drawable.ic_price)
        )
        
        binding.priceRecyclerView.adapter = PriceBoardAdapter(prices)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}