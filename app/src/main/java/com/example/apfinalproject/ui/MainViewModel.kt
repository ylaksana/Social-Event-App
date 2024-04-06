package com.example.apfinalproject.ui

import com.example.apfinalproject.event.EventList
import androidx.core.view.isGone
import androidx.lifecycle.ViewModel
import com.example.apfinalproject.databinding.ActionBarBinding
import com.example.apfinalproject.event.Event
import com.example.apfinalproject.user.User
import com.example.apfinalproject.interest.InterestCategories.Interest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData


class MainViewModel(): ViewModel() {
    private var actionBarBinding : ActionBarBinding? = null
    private var events: List<Event> = EventList.getAll()

    private fun createDummyUser(): User {
        // Dummy function to create a User object
        val user = User("User")
        user.bio = "This is a bio"
        user.profilePicture = "profilePicture"
        user.addInterest(Interest("Music", "Jazz"))
        user.addInterest(Interest("Movies", "Action"))
        user.addInterest(Interest("Food", "Italian"))
        user.addInterest(Interest("Sports", "Basketball"))

        val dummyEvents = EventList.getAll()
        user.addPastEvent(dummyEvents[0])
        user.addPastEvent(dummyEvents[1])
        return user
    }

    private var activeUser = MutableLiveData<User>().apply {
        this.postValue(createDummyUser())
    }

    fun observeActiveUser(): LiveData<User> {
        return activeUser
    }

    fun observePastEvents(): LiveData<MutableList<Event>> {
        val user = activeUser.value
        // I don't think this could ever be null, user is created after login
        return user!!.pastEvents
    }

    fun observeInterests(): LiveData<MutableList<Interest>> {
        val user = activeUser.value
        // I don't think this could ever be null, user is created after login
        return user!!.userInterests
    }

    fun observeEvents(): List<Event> {
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
}