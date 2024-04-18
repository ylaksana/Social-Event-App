package com.example.apfinalproject.chat

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.apfinalproject.MainViewModel
import com.example.apfinalproject.databinding.MessageRowBinding
import com.example.apfinalproject.ViewModelDBHelper

class ChatAdapter(private val viewModel: MainViewModel,
                  private val dbHelper: ViewModelDBHelper)
    : ListAdapter<Message, ChatAdapter.MessageViewHolder>(MessageDiff()) {


    inner class MessageViewHolder(val messageRowBinding: MessageRowBinding)
        : RecyclerView.ViewHolder(messageRowBinding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.MessageViewHolder {
        val messageRowBinding = MessageRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(messageRowBinding)
    }

    override fun onBindViewHolder(holder: ChatAdapter.MessageViewHolder, position: Int) {
        val messageRowBinding = holder.messageRowBinding
        val message = getItem(position)
        val activeUser = viewModel.getActiveUser()
        Log.d("ChatAdapter", "message sender: ${message.senderID}")

        if (activeUser != null) {
            if (message.senderID == activeUser.id) {
                messageRowBinding.chatMessageTV.gravity = Gravity.END
                messageRowBinding.chatTimeTV.gravity = Gravity.END
            } else {
                messageRowBinding.chatMessageTV.gravity = Gravity.START
                messageRowBinding.chatTimeTV.gravity = Gravity.START
            }
        }
        messageRowBinding.chatMessageTV.text = message.messageText

    }

    class MessageDiff : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.messageText == newItem.messageText &&
                    oldItem.senderID == newItem.senderID
        }
    }
}