package com.example.apfinalproject

import android.util.Log
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.lifecycle.ViewModel
import com.example.apfinalproject.databinding.ActionBarBinding
import com.example.apfinalproject.event.Event
import com.example.apfinalproject.user.User
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.apfinalproject.ViewModelDBHelper
import com.example.apfinalproject.glide.Glide
import com.example.apfinalproject.Storage
import com.example.apfinalproject.osm.OSMApi
import com.example.apfinalproject.osm.OSMLocation
import com.example.apfinalproject.osm.OSMRepository
import com.example.apfinalproject.user.invalidUser
import com.example.apfinalproject.user.invalidUserUid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception
import android.net.Uri

class MainViewModel(): ViewModel() {
    private val TAG = "MainViewModel"
    private var actionBarBinding : ActionBarBinding? = null
//    private var events: MutableLiveData<List<Event>> = MutableLiveData(EventList.getAll())
    private var activeUser: User = invalidUser
    private val storage = Storage()
    private val db = ViewModelDBHelper()
    private var photoUUID = ""


    private var events = MutableLiveData<List<Event>>().apply {
        viewModelScope.launch(Dispatchers.IO) {
            val events = db.fetchUpdatingEventList { fetchedEvents ->
                postValue(fetchedEvents)
            }
        }
    }


    // OSM
    private val osmAPI = OSMApi.create()
    private val osmRepository = OSMRepository(osmAPI)
    private var searchLocation = MutableLiveData<String>()

    private var netLocations =  MediatorLiveData<List<OSMLocation>>().apply{
        addSource(searchLocation) { term ->
            try {
                viewModelScope.launch(Dispatchers.IO) {
                    val locations = osmRepository.fetchLocation(term)
                    Log.d("OSM", "Fetched locations: $locations")
                    if (locations.isNotEmpty()) {
                        // Post the location to a LiveData object
                        postValue(locations)
                    }
                }
            } catch(e: HttpException) {
            Log.e("HTTP Error", "Error fetching posts: ${e.code()}")
            } catch(e: Exception) {
            Log.e("General Error", "Error in fetching from API: ${e.message}")
            }
        }
    }

    fun observeLocations(): LiveData<List<OSMLocation>> {
        Log.d("ObserveLocations", "Fetched locations: ${netLocations.value}")
        return netLocations
    }

    fun setLocationTerm(term: String) {
        searchLocation.postValue(term)
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
//                convertToEventAndPost(activeUser.pastEvents)
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
//    private fun convertToEventAndPost(eventIdList: List<String>) {
//        val eventList = mutableListOf<Event>()
//        viewModelScope.launch(Dispatchers.IO) {
//            eventIdList.map {eventID ->
//                // Fetches event on background thread
//                val event = async {
//                    db.fetchEventByUid(eventID) {fetchedEvent ->
//                        fetchedEvent?.let {
//                            eventList.add(it)
//                        }
//                    }
//                }
//                event.await()
//            }
//            pastEventsLiveData.postValue(eventList)
//        }
//    }

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

    fun addEvent(newEvent: Event) {
        db.createEvent(newEvent) {

        }
    }

    fun uploadImage(imageUri: Uri, collection: String, resultListener: (String) -> Unit) {
        storage.uploadImage(imageUri, collection, resultListener)
    }

    fun getUserEvents(uid: String, resultListener: (List<Event>) -> Unit) {
        db.fetchUserEvents(uid, resultListener)
    }
}
