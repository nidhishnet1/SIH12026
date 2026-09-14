package com.example.demosih11.ui.priceboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demosih11.databinding.ItemMaterialPriceBinding
import com.example.demosih11.model.MaterialPrice

class PriceBoardAdapter(private val items: List<MaterialPrice>) :
    RecyclerView.Adapter<PriceBoardAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMaterialPriceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMaterialPriceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.materialName.text = item.name
        holder.binding.materialPrice.text = item.price
        holder.binding.priceTrend.text = item.trend
        holder.binding.materialIcon.setImageResource(item.iconRes)
    }

    override fun getItemCount() = items.size
}