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
import com.example.apfinalproject.glide.Glide
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

class MainViewModel(): ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }
    private var actionBarBinding : ActionBarBinding? = null
//    private var events: MutableLiveData<List<Event>> = MutableLiveData(EventList.getAll())
    private val authID: MutableLiveData<String> = MutableLiveData()
    private val storage = Storage()
    private val db = ViewModelDBHelper()

    private var events = MutableLiveData<List<Event>>().apply {
        db.fetchEvents { eventList ->
            this.postValue(eventList)
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

    fun getStorage(): Storage {
        return storage
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
    var interestsLiveData = MutableLiveData<List<String>>().apply {
        this.postValue(listOf())
    }

    fun isUserInDB(uid: String) : LiveData<Boolean> {
        Log.d(TAG, "isUserInDB: $uid")
        val isUserExists = MutableLiveData<Boolean>()
        db.fetchUserByUid(uid) { result ->
            isUserExists.postValue(result != invalidUser)
        }
        return isUserExists
    }

    fun setAuthID(uid: String) {
        Log.d(TAG, "setActiveAuthUser: $uid")
        authID.value = uid
    }

    fun observeAuthID(): LiveData<String> {
        return authID
    }

    private var activeUser = MediatorLiveData<User>().apply {
        addSource(authID) { uid ->
            if (uid != invalidUserUid) {
                db.fetchUserByUid(uid) { user ->
                    if (user != null) {
                        this.postValue(user)
                    } else {
                        // User is not in database (new user)
                        this.postValue(null)
                    }
                }
            } else {
                // User is logged out of Firebase
                this.postValue(invalidUser)
            }
        }
    }

    fun observeActiveUser(): LiveData<User> {
        return activeUser
    }

    // MainActivity gets updates on this via live data and informs view model
    fun setActiveAuthUserID(uid: String) {
        Log.d(TAG, "setActiveAuthUser: $uid")
        if (uid != invalidUserUid) {
            db.fetchUserByUid(uid) {
                activeUser.postValue(it)
                Log.d(TAG, "setActiveAuthUser: ${activeUser.value?.displayName} ${activeUser.value?.email} ${activeUser.value?.uid} ${activeUser.value?.bio}")

                interestsLiveData.postValue(activeUser.value?.userInterests)
                convertToEventAndPost(activeUser.value?.pastEvents)
                db.fetchEvents { eventList ->
                    events.postValue(eventList)
                }
            }
            Log.d(TAG, "setActiveAuthUser: $uid")
        } else {
            activeUser.value = invalidUser
            pastEventsLiveData.postValue(listOf())
            interestsLiveData.postValue(listOf())
            Log.d(TAG, "setActiveAuthUser: invalid user")
        }
    }

    // Converts list of event IDs to list of events, then posts to live data
    private fun convertToEventAndPost(eventIdList: List<String>?) {
        val eventList = mutableListOf<Event>()
        CoroutineScope(Dispatchers.IO).launch {
            eventIdList?.map {eventID ->
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

    fun getActiveUser(): User? {
        return activeUser.value
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

    fun updateUser(newUser: User) {
        activeUser.value = newUser
        activeUser.value?.uid?.let { db.updateUser(it, newUser) }
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

    fun addNewUser(newUser: User) {
        Log.d(TAG, "addNewUser: ${newUser.uid}")
        db.createUser(newUser) { _ ->
            activeUser.postValue(newUser)
        }
    }
}
