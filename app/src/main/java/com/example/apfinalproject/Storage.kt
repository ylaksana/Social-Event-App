package com.example.apfinalproject

import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Storage {
    private val photoStorage: StorageReference =
        FirebaseStorage.getInstance().getReference("images")

    fun getUserPhoto(uuid: String): StorageReference {
        Log.d(javaClass.simpleName, "getUserPhoto: $uuid")
        return photoStorage.child("users/${uuid}.jpg")
    }

    fun getEventPhoto(uuid: String): StorageReference {
        Log.d(javaClass.simpleName, "getEventPhoto: $uuid")
        return photoStorage.child("events/${uuid}.jpg")
    }
}