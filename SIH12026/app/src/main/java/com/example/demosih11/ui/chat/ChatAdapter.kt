package com.example.demosih11.ui.chat

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.demosih11.databinding.ItemChatMessageBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val content: String,
    val isSentByMe: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.binding.textMessageContent.text = message.content
        
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        holder.binding.textTimestamp.text = sdf.format(Date(message.timestamp))

        val params = holder.binding.cardMessage.layoutParams as LinearLayout.LayoutParams
        val rootParams = holder.binding.layoutMessageRoot.layoutParams as RecyclerView.LayoutParams
        
        if (message.isSentByMe) {
            holder.binding.layoutMessageRoot.gravity = Gravity.END
            holder.binding.cardMessage.setCardBackgroundColor(Color.parseColor("#DCF8C6"))
            params.gravity = Gravity.END
        } else {
            holder.binding.layoutMessageRoot.gravity = Gravity.START
            holder.binding.cardMessage.setCardBackgroundColor(Color.WHITE)
            params.gravity = Gravity.START
        }
        
        holder.binding.cardMessage.layoutParams = params
    }

    override fun getItemCount(): Int = messages.size
}