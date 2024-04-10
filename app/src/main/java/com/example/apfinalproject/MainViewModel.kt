package com.example.apfinalproject

import android.util.Log
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.lifecycle.ViewModel
import com.example.apfinalproject.databinding.ActionBarBinding
import com.example.apfinalproject.event.Event
import com.example.apfinalproject.user.User
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.apfinalproject.glide.Glide
import com.example.apfinalproject.user.invalidUser
import com.example.apfinalproject.user.invalidUserUid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainViewModel(): ViewModel() {
    private val TAG = "MainViewModel"
    private var actionBarBinding : ActionBarBinding? = null
//    private var events: MutableLiveData<List<Event>> = MutableLiveData(EventList.getAll())
    private var activeUser: User = invalidUser
    private val storage = Storage()
    private val db = ViewModelDBHelper()

    private var events = MutableLiveData<List<Event>>().apply {
        db.fetchEvents { eventList ->
            this.postValue(eventList)
        }
    }

    // Convert these to Event/Interest objects later
    private var pastEventsLiveData = MutableLiveData<List<Event>>().apply {
        this.postValue(listOf())
    }
    private var interestsLiveData = MutableLiveData<List<String>>().apply {
        this.postValue(listOf())
    }
    


    // MainActivity gets updates on this via live data and informs view model
    fun setActiveAuthUser(uid: String) {
        if (uid != invalidUserUid) {
            db.fetchUserByUid(uid) {
                activeUser = it!!
                Log.d(TAG, "setActiveAuthUser: ${activeUser.displayName} ${activeUser.email} ${activeUser.uid} ${activeUser.bio}")

                interestsLiveData.postValue(activeUser.userInterests)
                convertToEventAndPost(activeUser.pastEvents)
            }
            Log.d(TAG, "setActiveAuthUser: $uid")
        } else {
            activeUser = invalidUser
            pastEventsLiveData.postValue(listOf())
            interestsLiveData.postValue(listOf())
            Log.d(TAG, "setActiveAuthUser: invalid user")
        }
    }

    // Converts list of event IDs to list of events, then posts to live data
    private fun convertToEventAndPost(eventIdList: List<String>) {
        val eventList = mutableListOf<Event>()
        CoroutineScope(Dispatchers.IO).launch {
            eventIdList.map {eventID ->
                // Fetches event on background thread
                val event = async {
                    db.fetchEventByUid(eventID) {fetchedEvent ->
                        fetchedEvent?.let {
                            eventList.add(it)
                        }
                    }
                }
                event.await()
            }
        }
        pastEventsLiveData.postValue(eventList)
    }

    fun getActiveUser(): User {
        return activeUser
    }

    fun observePastEvents(): LiveData<List<Event>> {
        return pastEventsLiveData
    }

    fun observeInterests(): LiveData<List<String>> {
        return interestsLiveData
    }

    fun observeEvents(): LiveData<List<Event>> {
        return events
    }

    fun initActionBarBinding(it: ActionBarBinding) {
        actionBarBinding = it
    }

    fun hideActionBar(){
        actionBarBinding?.root?.isGone = true
    }

    fun showActionBar(){
        actionBarBinding?.root?.isGone = false
    }

    fun fetchUserImage(uuid: String, imageView: ImageView) {
        Log.d(TAG, "fetchUserImage: $uuid")
        val path = storage.getUserPhoto(uuid)
        Log.d(TAG, "fetchUserImage: $path")
        Glide.fetch(path, imageView)
    }

    fun fetchEventImage(uuid: String, imageView: ImageView) {
        Log.d(TAG, "fetchEventImage: $uuid")
        val path = storage.getEventPhoto(uuid)
        Log.d(TAG, "fetchEventImage: $path")
        Glide.fetch(path, imageView)
    }
}