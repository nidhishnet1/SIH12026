package com.example.demosih11.ui.chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demosih11.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup message list
        chatAdapter = ChatAdapter(messageList)
        binding.rvChatMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChatMessages.adapter = chatAdapter
        
        binding.btnSendMessage.setOnClickListener {
            val msgText = binding.editChatMessage.text.toString().trim()
            if (msgText.isNotEmpty()) {
                sendMessage(msgText, true)
                binding.editChatMessage.text?.clear()
                
                // Simulate partner typing and replying after a short delay
                Handler(Looper.getMainLooper()).postDelayed({
                    sendMessage("Okay, I am on the way. Please keep it ready.", false)
                }, 2000)
            }
        }
        
        // Dynamic Role Detection (Simulated for this demo)
        val isRecycler = arguments?.getBoolean("IS_RECYCLER") ?: false
        if (messageList.isEmpty()) {
            if (isRecycler) {
                binding.textChatPartnerName.text = "Chat with Rajesh (Kabadi Wala)"
                binding.textEstimatedTime.text = "Partner ETA: 35 Mins"
                sendMessage("Hello, I have accepted your lot #4802.", false)
            } else {
                binding.textChatPartnerName.text = "Chat with GreenTech (Recycler)"
                binding.textEstimatedTime.text = "Time to Reach: 42 Mins"
                sendMessage("Hello, I will be reaching your location shortly.", false)
            }
        }
    }

    private fun sendMessage(text: String, isMe: Boolean) {
        messageList.add(ChatMessage(text, isMe))
        chatAdapter.notifyItemInserted(messageList.size - 1)
        binding.rvChatMessages.scrollToPosition(messageList.size - 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}