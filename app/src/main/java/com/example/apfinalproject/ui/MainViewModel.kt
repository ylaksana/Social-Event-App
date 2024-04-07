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
import com.example.apfinalproject.user.invalidUser


class MainViewModel(): ViewModel() {
    private var actionBarBinding : ActionBarBinding? = null
    private var events: List<Event> = EventList.getAll()
    private var activeUser = invalidUser

    private fun createDummyUser(): User {
        // Dummy function to create a User object
        val user = User("User", "fakeEmail", "fakeUid")
        user.bio = "This is a bio"
        user.profilePicture = "profilePicture"
        user.addInterest(Interest("Music", "Jazz"))
        user.addInterest(Interest("Movies", "Action"))
        user.addInterest(Interest("Food", "Italian"))
        user.addInterest(Interest("Sports", "Basketball"))
        user.addInterest(Interest("Music", "Rock"))
        user.addInterest(Interest("Movies", "Comedy"))
        user.addInterest(Interest("Food", "Mexican"))
        user.addInterest(Interest("Sports", "Soccer"))
        user.addInterest(Interest("Music", "Pop"))

        val dummyEvents = EventList.getAll()
        user.addPastEvent(dummyEvents[0])
        user.addPastEvent(dummyEvents[1])
        return user
    }

    // MainActivity gets updates on this via live data and informs view model
    fun setActiveAuthUser(user: User) {
        activeUser = user
    }

    fun getActiveUser(): User {
        return activeUser
    }

    fun observePastEvents(): LiveData<MutableList<Event>> {
        return activeUser.pastEvents
    }

    fun observeInterests(): LiveData<MutableList<Interest>> {
        return activeUser.userInterests
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