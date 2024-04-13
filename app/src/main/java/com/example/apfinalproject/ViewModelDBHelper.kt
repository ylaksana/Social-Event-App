package com.example.apfinalproject

import android.util.Log
import android.widget.Toast
import com.example.apfinalproject.chat.Conversation
import com.example.apfinalproject.event.Event
import com.example.apfinalproject.user.User
import com.example.apfinalproject.user.invalidUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewModelDBHelper {
    private val TAG = "ViewModelDBHelper"
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val queryLimit: Long = 100

    // Use .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
    // to listen for real time updates
    fun fetchUsers(resultListener: (List<User>) -> Unit) {
        Log.d(TAG, "fetchUsers started")
        val query = db.collection("users")
        Log.d(TAG, "query: users")
        query
            .limit(queryLimit)
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "users fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                resultListener(result.documents.mapNotNull {
                    it.toObject(User::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(TAG, "users fetch FAILED ", it)
                resultListener(listOf())
            }
    }

    // Fetches user by uid from "users" collection and converts it to a User object
    // Returns null if user is not found or if there is an error
    fun fetchUserByUid(
        uid: String,
        resultListener: (User?)->Unit
    ) {
        Log.d(TAG, "fetchUserByUid started for $uid")
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "fetchUserByUid succeeded for $uid")
                Log.d(TAG, "fetchUserByUid: ${result.toObject(User::class.java)}")
                // NB: This is done on a background thread
                if (result.toObject(User::class.java) == null) {
                    Log.d(TAG, "fetchUserByUid: user is null, doesn't not exist in db")
                    resultListener(invalidUser)
                } else {
                    Log.d(TAG, "fetchUserByUid: user is not null, exists in db")
                    resultListener(result.toObject(User::class.java))
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "fetchUserByUid: query failed", it)
                resultListener(null)
            }
    }

    fun createUser(
        user: User,
        resultListener: (List<User>)->Unit
    ) {
        Log.d(TAG, "createUser started")
        db.collection("users").document(user.uid).set(user)
            .addOnSuccessListener {
                Log.d(TAG, "createUser succeeded")
                fetchUsers(resultListener)
            }
            .addOnFailureListener {
                Log.d(TAG, "createUser failed", it)
                resultListener(listOf())
            }
    }

    fun removeUser(
        user: User,
        resultListener: (List<User>)->Unit
    ) {
        Log.d(TAG, "removeUser started")
        db.collection("users")
            .document(user.uid)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "removeUser succeeded")
                fetchUsers(resultListener)
            }
            .addOnFailureListener {
                Log.d(TAG, "removeUser failed", it)
                resultListener(listOf())
            }
    }

    fun createEvent(
        event: Event,
        resultListener: (List<Event>)->Unit
    ) {
        Log.d(TAG, "createEvent started")
        db.collection("events").document(event.uid).set(event)
            .addOnSuccessListener {
                Log.d(TAG, "createEvent succeeded")
                fetchEvents(resultListener)
            }
            .addOnFailureListener {
                Log.d(TAG, "createEvent failed", it)
                resultListener(listOf())
            }
    }

    fun fetchEvents(resultListener: (List<Event>) -> Unit) {
        Log.d(TAG, "fetchEvents started")
        val query = db.collection("events")
        Log.d(TAG, "query: events")
        query
            .limit(queryLimit)
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "events fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                resultListener(result.documents.mapNotNull {
                    it.toObject(Event::class.java)
                })

            }
            .addOnFailureListener {
                Log.d(TAG, "events fetch FAILED ", it)
                resultListener(listOf())
            }
    }

    // Fetches event by uid from "events" collection and converts it to an Event object
    // This is used for grabbing event IDs on a user object (and later converting them to events)
    fun fetchEventByUid(
        uid: String,
        resultListener: (Event?)->Unit
    ) {
        Log.d(TAG, "fetchEvent started")
        db.collection("events")
            .document(uid)
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "event fetch succeeded for $uid")
                // NB: This is done on a background thread
                resultListener(result.toObject(Event::class.java))
            }
            .addOnFailureListener {
                Log.d(TAG, "event fetch failed", it)
                resultListener(null)
            }
    }

    fun removeEvent(
        event: Event,
        resultListener: (List<Event>)->Unit
    ) {
        Log.d(TAG, "removeEvent started")
        db.collection("events")
            .document(event.uid)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "removeEvent succeeded")
                fetchEvents(resultListener)
            }
            .addOnFailureListener {
                Log.d(TAG, "removeEvent failed", it)
                resultListener(listOf())
            }
    }

    fun updateUser(
        userID: String,
        newUser: User
    ) {
        Log.d(TAG, "updateUser started")
        db.collection("users")
            .document(userID)
            .set(newUser)
            .addOnSuccessListener {
                Log.d(TAG, "updateUser succeeded")

            }
            .addOnFailureListener {
                Log.d(TAG, "updateUser failed", it)
            }
    }
}