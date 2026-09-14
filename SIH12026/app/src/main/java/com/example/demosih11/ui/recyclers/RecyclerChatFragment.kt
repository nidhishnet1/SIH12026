package com.example.demosih11.ui.recyclers

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demosih11.databinding.FragmentRecyclerChatBinding
import com.example.demosih11.ui.chat.ChatAdapter
import com.example.demosih11.ui.chat.ChatMessage

class RecyclerChatFragment : Fragment() {

    private var _binding: FragmentRecyclerChatBinding? = null
    private val binding get() = _binding!!

    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecyclerChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatAdapter = ChatAdapter(messageList)
        binding.rvRecyclerChatMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecyclerChatMessages.adapter = chatAdapter

        if (messageList.isEmpty()) {
            messageList.add(ChatMessage("Enterprise dispatch offer accepted. Awaiting collector shipment confirmation.", false))
            chatAdapter.notifyItemInserted(0)
        }

        binding.btnSendRecyclerMessage.setOnClickListener {
            val text = binding.editRecyclerMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendRecyclerMessage(text, true)
                binding.editRecyclerMessage.text?.clear()

                Handler(Looper.getMainLooper()).postDelayed({
                    sendRecyclerMessage("Confirmed. Dispatch vehicle is en route to plant.", false)
                }, 2000)
            }
        }
    }

    private fun sendRecyclerMessage(text: String, isMe: Boolean) {
        messageList.add(ChatMessage(text, isMe))
        chatAdapter.notifyItemInserted(messageList.size - 1)
        binding.rvRecyclerChatMessages.scrollToPosition(messageList.size - 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}