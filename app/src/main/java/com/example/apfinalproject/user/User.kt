package com.example.apfinalproject.user

import androidx.lifecycle.MutableLiveData
import com.example.apfinalproject.event.Event
import com.example.apfinalproject.interest.InterestCategories.Interest


class User(private val nullableName: String?,
           private val nullableEmail: String?,
           val uid: String) {
    val name: String = nullableName ?: "User logged out"
    val email: String = nullableEmail ?: "User logged out"

    var bio: String = "Wow... nothing's here yet!"
    var profilePicture: String = ""
    var userInterests: MutableLiveData<MutableList<Interest>> = MutableLiveData(mutableListOf())
    var pastEvents: MutableLiveData<MutableList<Event>> = MutableLiveData(mutableListOf())

    fun isInvalid(): Boolean {
        return this.uid == invalidUserUid
    }

    fun addInterest(interest: Interest) {
        if (!userInterests.value?.contains(interest)!!) {
            // Make sure that the interest is a valid interest and
            // that the user does not already have it
            userInterests.value?.add(interest)
        }
    }
    fun removeInterest(interest: Interest) {
        userInterests.value?.remove(interest)
    }
    fun addPastEvent(event: Event) {
        pastEvents.value?.add(event)
    }
    fun removePastEvent(event: Event) {
        pastEvents.value?.remove(event)
    }
}
const val invalidUserUid = "-1"
val invalidUser = User(null, null, invalidUserUid)
