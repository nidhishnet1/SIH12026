package com.example.demosih11.ui.admin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.demosih11.databinding.ItemPendingRegistrationBinding
import com.example.demosih11.model.RegistrationCache
import com.example.demosih11.model.RegistrationCache.UserRegistration
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class PendingRegsAdapter(private val list: MutableList<UserRegistration>) :
    RecyclerView.Adapter<PendingRegsAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    class ViewHolder(val binding: ItemPendingRegistrationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPendingRegistrationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        
        // Dynamically reflect changes and verification states explicitly
        if (item.isVerified) {
            holder.binding.textItemName.text = "${item.firstName} ${item.lastName}"
            holder.binding.textItemRole.text = "${item.role.uppercase()} (VERIFIED)"
            holder.binding.textItemRole.setBackgroundColor(Color.parseColor("#E8F5E9"))
            holder.binding.textItemRole.setTextColor(Color.parseColor("#2E7D32"))
            holder.binding.btnAdminVerifyUser.visibility = View.GONE
            holder.binding.btnAdminUnverifyUser.visibility = View.VISIBLE
        } else {
            holder.binding.textItemName.text = "${item.firstName} ${item.lastName}"
            holder.binding.textItemRole.text = item.role.uppercase()
            holder.binding.textItemRole.setBackgroundColor(Color.parseColor("#E1F5FE"))
            holder.binding.textItemRole.setTextColor(Color.parseColor("#0288D1"))
            holder.binding.btnAdminVerifyUser.visibility = View.VISIBLE
            holder.binding.btnAdminVerifyUser.text = "Verify Profile"
            holder.binding.btnAdminUnverifyUser.visibility = View.GONE
        }

        holder.binding.textItemContact.text = "Phone: ${item.phone} | Email: ${item.email}"
        holder.binding.textItemDocs.text = "Aadhar: ${item.aadharNo} | GST: ${item.gstNo}"
        if (!item.aadharPhotoUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.aadharPhotoUrl)
                .into(holder.binding.imgAadharCopy)
        } else {
            holder.binding.imgAadharCopy.setImageResource(com.example.demosih11.R.drawable.ic_launcher_background)
        }

        holder.binding.btnAdminVerifyUser.setOnClickListener {
            item.isVerified = true
            if (item.uid.isNotEmpty()) {
                db.collection("registrations").document(item.uid)
                    .update("isVerified", true)
            }
            Toast.makeText(holder.itemView.context, "Partner Profile Verified: ${item.firstName}", Toast.LENGTH_SHORT).show()
            notifyItemChanged(position)
        }

        holder.binding.btnAdminUnverifyUser.setOnClickListener {
            item.isVerified = false
            if (item.uid.isNotEmpty()) {
                db.collection("registrations").document(item.uid)
                    .update("isVerified", false)
            }
            Toast.makeText(holder.itemView.context, "Partner Profile Unverified: ${item.firstName}", Toast.LENGTH_SHORT).show()
            notifyItemChanged(position)
        }

        holder.binding.btnAdminEditUser.setOnClickListener {
            showEditDialog(holder, position)
        }
    }

    private fun showEditDialog(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = list[position]
        
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val editFirst = EditText(context).apply { 
            setText(item.firstName)
            hint = "First Name"
        }
        val editLast = EditText(context).apply { 
            setText(item.lastName)
            hint = "Last Name"
        }
        val editPhone = EditText(context).apply { 
            setText(item.phone)
            hint = "Phone No"
        }

        layout.addView(editFirst)
        layout.addView(editLast)
        layout.addView(editPhone)

        AlertDialog.Builder(context)
            .setTitle("Modify Registration Data")
            .setView(layout)
            .setPositiveButton("Update Record") { _, _ ->
                val updated = item.copy(
                    firstName = editFirst.text.toString(),
                    lastName = editLast.text.toString(),
                    phone = editPhone.text.toString()
                )
                list[position] = updated
                if (updated.uid.isNotEmpty()) {
                    db.collection("registrations").document(updated.uid)
                        .update(
                            mapOf(
                                "firstName" to updated.firstName,
                                "lastName" to updated.lastName,
                                "phone" to updated.phone
                            )
                        )
                }
                notifyItemChanged(position)
                Toast.makeText(context, "Database updated successfully", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Discard Changes", null)
            .show()
    }

    override fun getItemCount(): Int = list.size
}