package com.example.apfinalproject.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.ViewModelDBHelper
import com.example.apfinalproject.databinding.ChatFragmentBinding
import com.example.apfinalproject.chat.Message
import com.example.apfinalproject.chat.ChatAdapter
import com.example.apfinalproject.chat.ChatDBHelper
import androidx.recyclerview.widget.LinearLayoutManager

class ChatFragment: Fragment() {
    private val TAG = "ChatFragment"
    private val viewModel : MainViewModel by activityViewModels()
    private val chatDBHelper = ChatDBHelper()
    private val DBHelper = ViewModelDBHelper()
    private var chatAdapter: ChatAdapter? = null
    private var _binding: ChatFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController : NavController
    private val args: ChatFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        _binding = ChatFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        navController = findNavController()

        val conversationID = args.Conversation.conversationID

        chatAdapter = ChatAdapter(viewModel, ViewModelDBHelper())
        val activeUser = viewModel.getActiveUser()

        // I think this is unnecessary because users are already in the conversation arg
        if (activeUser != null) {
            chatDBHelper.getChatUsers(activeUser.uid, conversationID) { users ->
                val otherUser = users.filter { it != activeUser.uid }
                DBHelper.fetchUserByUid(otherUser[0]) {
                    binding.chatTitle.text = it?.firstName
                }
            }
        }

        // Can we get messages from the conversation arg?
        chatDBHelper.fetchMessages(conversationID) {messages ->
            chatAdapter!!.submitList(messages)
        }

        binding.sendButton.setOnClickListener(){
            val messageText = binding.messageET.text.toString()
            val message = activeUser?.let { it1 ->
                Message(
                    senderID = it1.uid,
                    messageText = messageText
                )
            }
            if (message != null) {
                chatDBHelper.uploadMessage(conversationID, message) {
                    Log.d(TAG, "Message uploaded")
                    if (it) {
                        chatAdapter!!.notifyItemChanged(chatAdapter!!.itemCount - 1)
                        binding.chatRV.scrollToPosition(chatAdapter!!.itemCount - 1)
                    } else {
                        Log.d(TAG, "Failed to upload message")
                    }
                    // Update the conversation's last message and timestamp
                }
            }
            binding.messageET.text.clear()
        }

        binding.chatRV.adapter = chatAdapter
        binding.chatRV.layoutManager = LinearLayoutManager(context)

        binding.backButton.setOnClickListener{
            navController.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}