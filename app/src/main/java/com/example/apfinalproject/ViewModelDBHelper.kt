package com.example.apfinalproject

import android.util.Log
import com.example.apfinalproject.event.Event
import com.example.apfinalproject.user.User
import com.example.apfinalproject.user.invalidUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID


class ViewModelDBHelper {
    private val TAG = "ViewModelDBHelper"
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val queryLimit: Long = 100

    // Use .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
    // to listen for real time updates

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

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
                val user = result.toObject(User::class.java)
                Log.d(TAG, "fetchUserByUid: ${user?.uid}")
                // NB: This is done on a background thread

                if (user == null) {
                    Log.d(TAG, "fetchUserByUid: user is null, doesn't not exist in db")
                    resultListener(invalidUser)
                } else {
                    Log.d(TAG, "fetchUserByUid: user is not null, exists in db")
                    resultListener(user)
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "fetchUserByUid: query failed", it)
                resultListener(invalidUser)
            }
    }

    fun createUser(
        user: User,
        resultListener: (User)->Unit
    ) {
        Log.d(TAG, "createUser started")
        db.collection("users").document(user.uid).set(user)
            .addOnSuccessListener {
                Log.d(TAG, "createUser succeeded")
                resultListener(user)
            }
            .addOnFailureListener {
                Log.d(TAG, "createUser failed", it)
                resultListener(invalidUser)
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
        resultListener: ()->Unit
    ) {
        Log.d(TAG, "createEvent started")
        val eventId = generateUUID()
        event.uid = eventId

        db.collection("events")
            .document(eventId)
            .set(event)
            .addOnSuccessListener {
                Log.d(TAG, "createEvent succeeded")
                resultListener()
            }
            .addOnFailureListener {
                Log.d(TAG, "createEvent failed", it)
                resultListener()
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

    fun fetchOthersUnswipedEvents(activeUserId: String,
                                  resultListener: (List<Event>) -> Unit) {
        Log.d(TAG, "fetchEvents started")
        val eventsRef = db.collection("events")
            .whereNotEqualTo("creator", activeUserId)
            .limit(queryLimit)

        Log.d(TAG, "query: events")
        eventsRef
            .get()
            .addOnSuccessListener { allEvents ->
                val unswipedEvents = allEvents.documents.mapNotNull {
                    it.toObject(Event::class.java)
                }.filter { event ->
                    Log.d(TAG, "event: ${event.uid} ${event.noSwipes} ${event.yesSwipes}")
                    activeUserId !in event.noSwipes && activeUserId !in event.yesSwipes
                }
                Log.d(TAG, "events fetch ${unswipedEvents.size}")
                resultListener(unswipedEvents)
            }
            .addOnFailureListener {
                Log.d(TAG, "events fetch FAILED ", it)
                resultListener(listOf())
            }
    }

    fun fetchUpdatingEventList(resultListener: (List<Event>) -> Unit) {
        Log.d(TAG, "fetchUpdatingEventList started")
        val query = db.collection("events")
        Log.d(TAG, "query: events snapshot")
        query
            .limit(queryLimit)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.d(TAG, "events fetch FAILED ", firebaseFirestoreException)
                    resultListener(listOf())
                    return@addSnapshotListener
                }

                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    Log.d(TAG, "events fetch ${querySnapshot.documents.size}")
                    // NB: This is done on a background thread
                    resultListener(querySnapshot.documents.mapNotNull {
                        it.toObject(Event::class.java)})
                } else {
                    Log.d(TAG, "events fetch FAILED ")
                    resultListener(listOf())
                }
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

    fun addEventSwipe(eventId: String, userId: String, direction: String) {
        Log.d(TAG, "addEventSwipe started for $eventId, $userId, $direction")
        db.collection("events")
            .document(eventId)
            .update(direction, FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
                Log.d(TAG, "addEventSwipe succeeded")
            }
            .addOnFailureListener {
                Log.d(TAG, "addEventSwipe failed", it)
            }
    }
}