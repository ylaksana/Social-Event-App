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

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        viewModel.hideActionBar()
        navController = findNavController()

        val conversationID = args.Conversation.id


        chatAdapter = ChatAdapter(viewModel, ViewModelDBHelper())
        val activeUser = viewModel.getActiveUser()

        // I think this db call is unnecessary because users are already in the conversation arg
        if (activeUser != null) {
            chatDBHelper.getChatUsers(activeUser.id, conversationID) { users ->
                val otherUser = users.filter { it != activeUser.id }
                DBHelper.fetchUserByUid(otherUser[0]) {
                    binding.chatTitle.text = it?.firstName
                }
            }
        }

        chatDBHelper.fetchMessages(conversationID) {messages ->
            chatAdapter?.submitList(messages)
            binding.chatRV.scrollToPosition(chatAdapter!!.itemCount - 1)
        }

        binding.sendButton.setOnClickListener(){
            val messageText = binding.messageET.text.toString()

            if (messageText.isEmpty()) {
                return@setOnClickListener
            }

            val message = activeUser?.let { it1 ->
                Message(
                    senderID = it1.id,
                    messageText = messageText,
                )
            }
            if (message != null) {
                chatDBHelper.uploadMessage(conversationID, message) {
                    Log.d(TAG, "Message uploaded")
                    if (it) {
                        chatAdapter?.notifyItemChanged(chatAdapter!!.itemCount - 1)
                        binding.chatRV.scrollToPosition(chatAdapter!!.itemCount - 1)
                    } else {
                        Log.d(TAG, "Failed to upload message")
                    }
                    // Update the conversation's last message and timestamp
                    chatDBHelper.updateConversationLastMessage(conversationID, message) {
                        Log.d(TAG, "Conversation updated")
                    }
                }
            }
            binding.messageET.text.clear()
        }

        binding.chatRV.adapter = chatAdapter
        binding.chatRV.layoutManager = LinearLayoutManager(context)



        binding.backButton.setOnClickListener{
            navController.navigate(ChatFragmentDirections.actionChatFragmentToChatListFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}