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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel(): ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }
    init {
        Log.d(TAG, ">>init")
    }
    private var actionBarBinding : ActionBarBinding? = null
    private val storage = Storage()
    private val db = ViewModelDBHelper()


    var activeUser = MutableLiveData<User>().apply {
        Log.d(TAG, ">>activeUser init")
        invalidUser
    }

    private var events = MediatorLiveData<List<Event>>().apply {
        Log.d(TAG, ">>events init")
        value = listOf()
        addSource(activeUser) { _ ->
            Log.d(TAG, ">>events activeUser changed")
            db.fetchEvents { eventList ->
                Log.d(TAG, ">>events activeUser changed, fetched ${eventList.size} events")
                postValue(eventList)
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

    var interestsLiveData = MediatorLiveData<List<String>>().apply {
        value = listOf()
        addSource(activeUser) { user ->
            this.postValue(user.userInterests)
        }
    }

    fun verifyUserAndLogin(userId: String, resultListener: (User) -> Unit) {
        Log.d(TAG, "checking isUserInDB: $userId")
        db.fetchUserByUid(userId) { result ->
            Log.d(TAG, "isUserInDB: ${result?.uid} : ${result?.displayName}")
            if (result != invalidUser) {
                Log.d(TAG, "user exists in db")
                activeUser.postValue(result)
            } else {
                Log.d(TAG, "isUserInDB: user is valid")
                activeUser.postValue(invalidUser)
            }
            if (result != null) {
                resultListener(result)
            }
        }
//        Log.d(TAG, "isUserInDB end: ${user.value?.uid}")
    }

    fun observeActiveUser(): LiveData<User> {
        return activeUser
    }

    // Converts list of event IDs to list of events, then posts to live data
    // TODO: add exception catches here
    private fun convertToEventAndPost(eventIdList: List<String>?) {
        val eventList = mutableListOf<Event>()
        viewModelScope.launch(Dispatchers.IO) {
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
        db.createUser(newUser) { user ->
            Log.d(TAG, "addNewUser: ${user.uid}")
            activeUser.postValue(user)
        }
    }
}
