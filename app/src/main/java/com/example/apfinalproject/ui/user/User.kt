package com.example.apfinalproject.ui.user

import com.example.apfinalproject.model.Event
class User(var name: String) {
    var bio: String = "Wow... nothing's here yet!"
    var profilePicture: String = ""
    var interests: ArrayList<String> = ArrayList()
    var pastEvents: ArrayList<Event> = ArrayList()

    fun addInterest(interest: String) {
        interests.add(interest)
    }
    fun removeInterest(interest: String) {
        interests.remove(interest)
    }
}