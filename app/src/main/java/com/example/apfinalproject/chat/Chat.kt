package com.example.apfinalproject.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

@Parcelize
data class Conversation(
    val id: String = "null conversation",
    val userIDs: List<String> = listOf(),
    val lastMessage: String = "",
    val messages: List<Message> = listOf(),
    val lastMessageTimestamp: Timestamp? = null
) : Parcelable

@Parcelize
data class Message(
    val senderID: String = "",
    val messageText: String = "",
    @DocumentId val id: String = "",
    @ServerTimestamp val timestamp: Timestamp? = null,
) : Parcelable