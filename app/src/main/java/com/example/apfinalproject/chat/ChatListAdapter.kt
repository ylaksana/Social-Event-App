package com.example.apfinalproject.chat

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.databinding.ChatListRowBinding
import com.example.apfinalproject.ViewModelDBHelper
import com.example.apfinalproject.user.invalidUser
import com.example.apfinalproject.chat.Conversation


class ChatListAdapter(private val viewModel: MainViewModel,
                      private val dbHelper: ViewModelDBHelper,
                      private val navigateToChat: (Conversation) -> Unit)
    : ListAdapter<Conversation, ChatListAdapter.ChatListViewHolder>(ChatListDiff()) {

    inner class ChatListViewHolder(val chatListRowBinding: ChatListRowBinding)
        : RecyclerView.ViewHolder(chatListRowBinding.root) {
        init {
            chatListRowBinding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val conversation = getItem(position)
                    navigateToChat(conversation)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListAdapter.ChatListViewHolder {
        val chatListRowBinding = ChatListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatListViewHolder(chatListRowBinding)
    }

    override fun onBindViewHolder(holder: ChatListAdapter.ChatListViewHolder, position: Int) {
        val chatListRowBinding = holder.chatListRowBinding
        val conversation = getItem(position)
        val activeUser = viewModel.getActiveUser()
        val otherUser = conversation.userIDs.find { it != (activeUser?.uid ?: "-1") }
        Log.d("ChatListAdapter", "Binding conversationID: ${conversation.conversationID}")

        if (otherUser != null) {
            dbHelper.fetchUserByUid(otherUser) { user ->
                if (user != invalidUser) {
                    chatListRowBinding.chatUserNameTV.text = user!!.firstName
                } else {
                    chatListRowBinding.chatUserNameTV.text = "Unknown User"
                }
            }
        }
        chatListRowBinding.chatMessageTV.text = conversation.lastMessage
    }

    class ChatListDiff : DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem.conversationID == newItem.conversationID
        }

        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem.conversationID == newItem.conversationID &&
//                    oldItem.userIDs == newItem.userIDs &&
//                    oldItem.messages == newItem.messages &&
                    oldItem.lastMessage == newItem.lastMessage
        }
    }
}