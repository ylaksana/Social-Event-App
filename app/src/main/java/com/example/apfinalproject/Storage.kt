package com.example.apfinalproject

import android.util.Log
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.util.UUID

class Storage {
    private val photoStorage: StorageReference =
        FirebaseStorage.getInstance().getReference("images")

    fun getUserPhoto(uuid: String): StorageReference {
        Log.d(javaClass.simpleName, "getUserPhoto: $uuid")
        return photoStorage.child("users/${uuid}")
    }

    fun getEventPhoto(uuid: String): StorageReference {
        Log.d(javaClass.simpleName, "getEventPhoto: $uuid")
        return photoStorage.child("events/${uuid}")
    }

    fun uploadUserPhoto(
        imageUri: Uri,
        newImageUUID: String,
        oldImageUUID: String,
        onComplete: () -> Unit
        ) {
        Log.d(javaClass.simpleName, "uploadUserPhoto: $newImageUUID")
        val imageRef = photoStorage.child("users/${newImageUUID}")
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()
        val uploadTask = imageRef.putFile(imageUri, metadata)

        uploadTask
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "uploadUserPhoto FAILED $newImageUUID")
            }
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "uploadUserPhoto succeeded $newImageUUID")
                if (oldImageUUID != "") {
                    removeUserPhoto(oldImageUUID)
                }
                onComplete()
            }
    }

    fun removeUserPhoto(uuid: String) {
        Log.d(javaClass.simpleName, "removeUserPhoto: $uuid")
        val imageRef = photoStorage.child("users/${uuid}")
        imageRef.delete()
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "removeUserPhoto succeeded $uuid")
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "removeUserPhoto failed $uuid")
            }
    }
}