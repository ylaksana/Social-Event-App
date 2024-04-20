package com.example.apfinalproject

import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.core.view.isGone
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apfinalproject.chat.Conversation
import com.example.apfinalproject.databinding.ActionBarBinding
import com.example.apfinalproject.event.Event
import com.example.apfinalproject.glide.Glide
import com.example.apfinalproject.osm.OSMApi
import com.example.apfinalproject.osm.OSMLocation
import com.example.apfinalproject.osm.OSMRepository
import com.example.apfinalproject.user.User
import com.example.apfinalproject.user.invalidUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class MainViewModel : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    private var actionBarBinding: ActionBarBinding? = null
    private val storage = Storage()
    private val db = ViewModelDBHelper()
    private val filterTerm: MutableLiveData<String> = MutableLiveData()
    private var isMyEvents: MutableLiveData<Boolean> = MutableLiveData()
    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> get() = _location

    fun updateLocation(location: Location) {
        _location.postValue(location)
    }

    fun observeUserLocation(): LiveData<Location> {
        return location
    }

    fun getDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    var activeUser =
        MutableLiveData<User>().apply {
            Log.d(TAG, ">>activeUser init")
            invalidUser
        }

    private var events = MutableLiveData<List<Event>>()

    private var nonUserEvents =
        MediatorLiveData<List<Event>>().apply {
            addSource(events) { originalList ->
                val filteredList =
                    originalList.filter { event ->
                        Log.d(TAG, "Filtering events: ${event.title}")
                        Log.d(TAG, "Active user: ${activeUser.value?.id}")
                        Log.d(TAG, "Event.yesSwipes: ${event.yesSwipes}")
                        Log.d(TAG, "event.noSwipes: ${event.noSwipes}")
                        event.creator != activeUser.value?.id
                    }
                Log.d("NonUserEvents", "Fetched events: $filteredList")
                postValue(filteredList)
            }
        }

    // OSM
    private val osmAPI = OSMApi.create()
    private val osmRepository = OSMRepository(osmAPI)
    private var searchLocation = MutableLiveData<String>()

    private var netLocations =
        MediatorLiveData<List<OSMLocation>>().apply {
            addSource(nonUserEvents) { events ->
                Log.d("OSM", "Fetching locations")
                try {
                    viewModelScope.launch(Dispatchers.IO) {
                        val locations = osmRepository.fetchLocations(events)
                        Log.d("OSM", "Fetched locations: $locations")
                        if (locations.isNotEmpty()) {
                            // Post the location to a LiveData object
                            postValue(locations)
                        }
                    }
                } catch (e: HttpException) {
                    Log.e("HTTP Error", "Error fetching posts: ${e.code()}")
                } catch (e: Exception) {
                    Log.e("General Error", "Error in fetching from API: ${e.message}")
                }
            }
        }

    private var netTypeEvents =
        MediatorLiveData<List<Event>>().apply {
            addSource(nonUserEvents) { events ->
                val term = filterTerm.value
                var filteredEvents = events
                if (!term.isNullOrEmpty()) {
                    filteredEvents =
                        events.filter { event ->
                            event.type == term
                        }
                }
                postValue(filteredEvents)
            }
            addSource(filterTerm) { term ->
                val events = nonUserEvents.value
                var filteredEvents = events
                if (!term.isNullOrEmpty() && events != null) {
                    filteredEvents =
                        events.filter { event ->
                            event.type == term
                        }
                }
                postValue(filteredEvents)
            }
        }

    private var netUserEvents =
        MediatorLiveData<List<Event>>().apply {
            addSource(isMyEvents) { switch ->
                var userEvents = nonUserEvents.value
                if (switch) {
                    userEvents =
                        events.value?.filter { event ->
                            event.creator == activeUser.value?.id
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

    fun observeAllEvents(): LiveData<List<Event>> {
        return events
    }

    fun setFilter(filter: String) {
        filterTerm.value = filter
        Log.d(TAG, "Filter: $filter")
    }

    fun setEvents(switch: Boolean) {
        isMyEvents.value = switch
    }

    // Convert these to Event/Interest objects later
    private var pastEventsLiveData =
        MutableLiveData<List<Event>>().apply {
            this.postValue(listOf())
        }

    var interestsLiveData =
        MediatorLiveData<List<String>>().apply {
            value = listOf()
            addSource(activeUser) { user ->
                postValue(user.userInterests)
            }
        }

    /** Checks if the user has already created a profile, then sets active user
     * @param userId: String - the user's id
     * @param resultListener: (User) -> Unit - navigates to CreateUserFrag if user is invalidUser
     */
    fun setActiveUser(
        userId: String,
        resultListener: (User) -> Unit,
    ) {
        Log.d(TAG, "checking isUserInDB: $userId")
        db.fetchUserByUid(userId) { user ->
            Log.d(TAG, "isUserInDB: ${user?.id} : ${user?.displayName}")
            if (user != invalidUser) {
                Log.d(TAG, "user exists in db")
                activeUser.postValue(user)
                db.fetchEvents { eventList ->
                    events.postValue(eventList)
                }
                interestsLiveData.postValue(user?.userInterests)
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

    fun initActionBarBinding(it: ActionBarBinding) {
        actionBarBinding = it
    }

    fun hideActionBar() {
        actionBarBinding?.root?.isGone = true
    }

    fun showActionBar() {
        actionBarBinding?.root?.isGone = false
    }

    fun updateUser(newUser: User) {
        activeUser.value = newUser
        activeUser.value?.id?.let { db.updateUser(it, newUser) }
    }

    fun fetchUserImage(
        uuid: String,
        imageView: ImageView,
    ) {
        Log.d(TAG, "fetchUserImage: $uuid")
        val path = storage.getUserPhoto(uuid)
        Log.d(TAG, "fetchUserImage: $path")
        Glide.fetch(path, imageView)
    }

    fun calculateDistanceInMiles(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
    ): Double {
        val earthRadius = 3958.75 // in miles, change to 6371 for kilometer output

        val dLat = Math.toRadians((lat2 - lat1))
        val dLng = Math.toRadians((lon2 - lon1))

        val sindLat = sin(dLat / 2)
        val sindLng = sin(dLng / 2)

        val a = sindLat.pow(2.0) + (sindLng.pow(2.0) * cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)))

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    fun fetchEventImage(
        uuid: String,
        imageView: ImageView,
    ) {
        Log.d(TAG, "fetchEventImage: $uuid")
        val path = storage.getEventPhoto(uuid)
        Log.d(TAG, "fetchEventImage: $path")
        Glide.fetch(path, imageView)
    }

    fun addUser(newUser: User) {
        Log.d(TAG, "addNewUser: ${newUser.id}")
        db.createUser(newUser) { user ->
            Log.d(TAG, "addNewUser: ${user.id}")
            activeUser.postValue(user)
        }
    }

    fun addEvent(newEvent: Event) {
        db.createEvent(newEvent) {}
    }

    fun uploadImage(
        imageUri: Uri,
        collection: String,
        resultListener: (String) -> Unit,
    ) {
        Log.d(TAG, "uploadImage to $collection: $imageUri")
        viewModelScope.launch(Dispatchers.IO) {
            storage.uploadImage(imageUri, collection, resultListener)
        }
    }

    fun deleteImage(
        uuid: String,
        collection: String,
    ) {
        Log.d(TAG, "deleteImage: $uuid")
        viewModelScope.launch(Dispatchers.IO) {
            storage.deleteImage(uuid, collection)
        }
    }

    fun addEventSwipe(
        eventId: String,
        direction: Int,
    ) {
        when (direction) {
            4 -> db.addEventSwipe(eventId, activeUser.value?.id!!, "noSwipes")
            8 -> db.addEventSwipe(eventId, activeUser.value?.id!!, "yesSwipes")
        }
    }

    fun removeEventFromView(event: Event) {
        val currentEvents = netTypeEvents.value?.toMutableList()
        currentEvents?.remove(event)
        netTypeEvents.postValue(currentEvents)
    }

    fun fetchUserByUid(
        uid: String,
        resultListener: (User?) -> Unit,
    ) {
        db.fetchUserByUid(uid) {
            resultListener(it)
        }
    }

    fun fetchUsersByUids(
        uids: List<String>,
        resultListener: (List<User>) -> Unit,
    ) {
        db.fetchUsersByUids(uids) {
            resultListener(it)
        }
    }

    fun fetchMyEvents(resultListener: (List<Event>) -> Unit) {
        Log.d(TAG, "fetchMyEvents: ${activeUser.value?.id}")
        if (activeUser.value != invalidUser && activeUser.value != null) {
            db.fetchMyEvents(activeUser.value?.id!!) {
                resultListener(it)
            }
        }
    }

    fun enterChatRoom(
        userId: String,
        otherUserId: String,
        resultListener: (Conversation) -> Unit,
    ) {
        Log.d(TAG, "enterChatRoom start")
        Log.d(TAG, "searching for chat room: $userId, $otherUserId")
        db.findChatRoom(userId, otherUserId) { conv ->
            conv?.let {
                Log.d(TAG, "chat room found ${conv.id}")
                resultListener(conv)
            } ?: run {
                Log.d(TAG, "chat room not found. Creating one.")
                createChatRoom(userId, otherUserId) { conv ->
                    resultListener(conv)
                }
            }
        }
    }

    private fun createChatRoom(
        userId: String,
        otherUserId: String,
        resultListener: (Conversation) -> Unit,
    ) {
        Log.d(TAG, "createChatRoom start")
        Log.d(TAG, "creating chat room: $userId, $otherUserId")
        db.createChatRoom(userId, otherUserId) { conv ->
            Log.d(TAG, "chat room created ${conv.id}")
            db.addConversationIDtoUser(userId, conv.id)
            db.addConversationIDtoUser(otherUserId, conv.id)
            resultListener(conv)
        }
    }

    fun updateEvent(event: Event) {
        Log.d(TAG, "updateEvent: ${event.id}")
        db.updateEvent(event)
    }

    suspend fun getEventCoords(locationName: String): OSMLocation {
        val locationCoords = osmRepository.fetchLocation(locationName)
        Log.d("OSM", "Fetched location: $location")
        return locationCoords
    }

    fun fetchConversationsByUserID(
        userID: String,
        resultListener: (List<Conversation>) -> Unit,
    ) {
        db.fetchUserConvIDs(userID) { convIDs ->
            if (convIDs.isEmpty()) {
                resultListener(listOf())
            } else {
                db.fetchConvsByIDs(convIDs) { convs ->
                    resultListener(convs)
                }
            }
        }
    }
}
