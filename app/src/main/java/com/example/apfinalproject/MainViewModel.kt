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
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception
import android.net.Uri
import com.example.apfinalproject.interest.InterestCategories.Interest

class MainViewModel: ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }
    init {
        Log.d(TAG, ">>init")
    }
    private var actionBarBinding : ActionBarBinding? = null
    private val storage = Storage()
    private val db = ViewModelDBHelper()
    private val filterTerm: MutableLiveData<String> = MutableLiveData()
    private var isMyEvents: MutableLiveData<Boolean> = MutableLiveData()


    var activeUser = MutableLiveData<User>().apply {
        Log.d(TAG, ">>activeUser init")
        invalidUser
    }
      
    private var events = MutableLiveData<List<Event>>().apply {
        viewModelScope.launch(Dispatchers.IO) {
            db.fetchUpdatingEventList { fetchedEvents ->
                postValue(fetchedEvents)
            }
        }
    }


    private var nonUserEvents = MediatorLiveData<List<Event>>().apply {
        addSource(events) { originalList ->
            val filteredList = originalList.filter { event ->
                // Replace "userCondition" with the condition you want to filter by
                event.creator != activeUser.value?.uid
            }
            postValue(filteredList)
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


    private var netTypeEvents = MediatorLiveData<List<Event>>().apply {
        addSource(nonUserEvents) { events ->
            val term = filterTerm.value
            var filteredEvents = events
            if (!term.isNullOrEmpty()) {
                filteredEvents = events.filter { event ->
                    event.type == term
                }
            }
            postValue(filteredEvents)
        }
        addSource(filterTerm) { term ->
            val events = nonUserEvents.value
            var filteredEvents = events
            if (!term.isNullOrEmpty() && events != null) {
                filteredEvents = events.filter { event ->
                    event.type == term
                }
            }
            postValue(filteredEvents)
        }
    }

    private var netUserEvents = MediatorLiveData<List<Event>>().apply {
        addSource(isMyEvents) { switch ->
            var userEvents = nonUserEvents.value
            if (switch) {
                userEvents = events.value?.filter { event ->
                    event.creator == activeUser.value?.uid
                }
            }
            postValue(userEvents)
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


    fun observeUserEvents(): LiveData<List<Event>> {
        return netUserEvents
    }

    fun observeNetTypeEvents(): LiveData<List<Event>> {
        return netTypeEvents
    }

    fun setFilter(filter: String){
        filterTerm.value = filter
        Log.d(TAG, "Filter: $filter")
    }

    fun setEvents(switch: Boolean){
            isMyEvents.value = switch
    }
    

    // Convert these to Event/Interest objects later
    private var pastEventsLiveData = MutableLiveData<List<Event>>().apply {
        this.postValue(listOf())
    }


//     // MainActivity gets updates on this via live data and informs view model
//     fun setActiveAuthUser(uid: String) {
//         if (uid != invalidUserUid) {
//             db.fetchUserByUid(uid) {
//                 activeUser = it!!
//                 userUID.postValue(it.uid)
//                 Log.d("ActiveUser", "setActiveAuthUser: ${activeUser.displayName} ${activeUser.email} ${activeUser.uid} ${activeUser.bio}")

//                 interestsLiveData.postValue(activeUser.userInterests)
//                 convertToEventAndPost(activeUser.pastEvents)
//             }
//             Log.d(TAG, "setActiveAuthUser: $uid")
//         } else {
//             activeUser = invalidUser
//             pastEventsLiveData.postValue(listOf())
//             interestsLiveData.postValue(listOf())
//             Log.d(TAG, "setActiveAuthUser: invalid user")

    var interestsLiveData = MediatorLiveData<List<String>>().apply {
        value = listOf()
        addSource(activeUser) { user ->
            postValue(user.userInterests)
        }
    }

    /** Checks if the user has already created a profile, then sets active user
     * @param userId: String - the user's id
     * @param resultListener: (User) -> Unit - navigates to CreateUserFrag if user is invalidUser
     */
    fun setActiveUser(userId: String, resultListener: (User) -> Unit) {
        Log.d(TAG, "checking isUserInDB: $userId")
        db.fetchUserByUid(userId) { user ->
            Log.d(TAG, "isUserInDB: ${user?.uid} : ${user?.displayName}")
            if (user != invalidUser) {
                Log.d(TAG, "user exists in db")
                activeUser.postValue(user)
            } else {
                Log.d(TAG, "user is invalid")
                activeUser.postValue(invalidUser)
            }
            if (user != null) {
                resultListener(user)
            }
        }
    }

    fun observeActiveUser(): LiveData<User> {
        return activeUser
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


    fun addUser(newUser: User) {
        Log.d(TAG, "addNewUser: ${newUser.uid}")
        db.createUser(newUser) { user ->
            Log.d(TAG, "addNewUser: ${user.uid}")
            activeUser.postValue(user)
        }
    }

    fun addEvent(newEvent: Event) {
        db.createEvent(newEvent) {}
    }

    fun uploadImage(imageUri: Uri, collection: String, resultListener: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            storage.uploadImage(imageUri, collection, resultListener)
        }
    }
}
