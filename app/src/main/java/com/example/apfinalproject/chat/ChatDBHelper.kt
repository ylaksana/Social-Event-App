package com.example.apfinalproject.chat

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import android.util.Log
import com.example.apfinalproject.user.User
import com.google.firebase.Timestamp



class ChatDBHelper {
    private val TAG = "ChatDBHelper"
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "chats"


    fun fetchMessages(
        conversationID: String,
        resultListener: (List<Message>) -> Unit
    ) {
        db.collection(rootCollection)
            .document(conversationID)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.d(TAG, "fetchMessages: failed")
                    resultListener(listOf())
                } else {
                    Log.d(TAG, "fetchMessages: succeeded")
                    resultListener(querySnapshot!!.documents.mapNotNull {
                        it.toObject(Message::class.java)
                    })
                }
            }
    }

    fun fetchConversationIDsByUserID(
        userID: String,
        resultListener: (List<String>)->Unit
    ) {
        Log.d(TAG, "fetchConversationsByUserID started")
        db.collection("users")
            .document(userID)
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "conversations fetch succeeded for $userID")
                // NB: This is done on a background thread
                resultListener(result.toObject(User::class.java)?.conversationIDs ?: listOf())
            }
            .addOnFailureListener {
                Log.d(TAG, "conversations fetch failed", it)
                resultListener(listOf())
            }
    }

    fun fetchConversationByID(
        conversationIDs: List<String>,
        resultListener: (List<Conversation>) -> Unit
    ) {
        //TODO: use where to fetch all conversations at once
        Log.d(TAG, "fetchConversationByID started $conversationIDs")
        val conversations = mutableListOf<Conversation>()
        conversationIDs.forEach { conversationID ->
            db.collection(rootCollection)
                .document(conversationID)
                .get()
                .addOnSuccessListener { result ->
                    Log.d(TAG, "fetchConversationByID: succeeded")
                    conversations.add(result.toObject(Conversation::class.java)!!)
                    if (conversations.size == conversationIDs.size) {
                        resultListener(conversations)
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "fetchConversationByID: failed")
                    conversations.add(Conversation())
                }
        }
    }

    fun getChatUsers(
        userID: String,
        conversationID: String,
        resultListener: (List<String>) -> Unit
    ) {
        db.collection(rootCollection)
            .document(conversationID)
            .get()
            .addOnSuccessListener { result ->
                val conversation = result.toObject(Conversation::class.java)
                if (conversation?.userIDs != null) {
                    Log.d(TAG, "getChatUsers: conversation is not null")
                    resultListener(conversation.userIDs)
                } else {
                    Log.d(TAG, "getChatUsers: conversation or userIDs is null")
                    resultListener(listOf())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "getChatUsers: failed")
                resultListener(listOf())
            }
    }

    fun uploadMessage(
        conversationID: String,
        message: Message,
        resultListener: (Boolean) -> Unit
    ) {
        val senderID = message.senderID
        Log.d(TAG, "uploadMessage: started ${message.messageText} $conversationID $senderID")
        db.collection(rootCollection)
            .document(conversationID)
            .collection("messages")
            .add(message)
            .addOnSuccessListener {
                Log.d(TAG, "uploadMessage: succeeded ${it.id}")
                resultListener(true)
            }
            .addOnFailureListener {
                Log.d(TAG, "uploadMessage: failed")
                resultListener(false)
            }
    }

}