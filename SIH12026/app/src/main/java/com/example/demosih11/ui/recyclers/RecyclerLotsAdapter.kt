package com.example.demosih11.ui.recyclers

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demosih11.R
import com.example.demosih11.databinding.ItemRecyclerLotBinding
import com.google.firebase.firestore.DocumentSnapshot

class RecyclerLotsAdapter(private var list: List<DocumentSnapshot>, private val onAcceptClicked: (DocumentSnapshot) -> Unit) :
    RecyclerView.Adapter<RecyclerLotsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRecyclerLotBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecyclerLotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doc = list[position]
        
        val category = doc.getString("category") ?: "Unknown"
        val weight = doc.getDouble("weight") ?: 0.0
        val price = doc.getDouble("estimatedValue") ?: 0.0
        val photoUrl = doc.getString("photoUrl")
        
        holder.binding.txtLotTitle.text = "Lot - $category"
        holder.binding.txtLotWeight.text = "$weight Kg"
        holder.binding.txtLotPrice.text = "Est. Price: ₹%.2f".format(price)
        holder.binding.txtKabadiwalaId.text = "Offered by: Kabadi Wala (Verified)"

        if (!photoUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(photoUrl)
                .into(holder.binding.imgLotPhoto)
        } else {
            holder.binding.imgLotPhoto.setImageResource(R.drawable.ic_launcher_background)
        }

        holder.binding.btnAcceptBid.setOnClickListener {
            onAcceptClicked(doc)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateList(newList: List<DocumentSnapshot>) {
        list = newList
        notifyDataSetChanged()
    }
}