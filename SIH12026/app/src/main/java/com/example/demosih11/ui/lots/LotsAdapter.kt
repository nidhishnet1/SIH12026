package com.example.demosih11.ui.lots

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.demosih11.databinding.ItemLotBinding
import com.example.demosih11.db.Lot

class LotsAdapter : ListAdapter<Lot, LotsAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(val binding: ItemLotBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lot = getItem(position)
        holder.binding.txtCategory.text = lot.category
        holder.binding.txtWeight.text = "${lot.weight} kg"
        holder.binding.txtValue.text = "₹ %.2f".format(lot.estimatedValue)
        holder.binding.txtStatus.text = lot.status
    }

    class DiffCallback : DiffUtil.ItemCallback<Lot>() {
        override fun areItemsTheSame(oldItem: Lot, newItem: Lot) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Lot, newItem: Lot) = oldItem == newItem
    }
}