package com.example.apfinalproject

import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.net.Uri
import com.google.firebase.storage.StorageMetadata
import java.util.UUID

class Storage {
    companion object {
        private val TAG = "Storage"
    }
    private val photoStorage: StorageReference =
        FirebaseStorage.getInstance().getReference("images")

    private fun generateFileName(): String {
        return UUID.randomUUID().toString()
    }

    fun getUserPhoto(uuid: String): StorageReference {
        Log.d(javaClass.simpleName, "getUserPhoto: $uuid")
        return photoStorage.child("users/${uuid}.jpg")
    }

    fun getEventPhoto(uuid: String): StorageReference {
        Log.d(javaClass.simpleName, "getEventPhoto: $uuid")
        return photoStorage.child("events/${uuid}.jpg")
    }

    fun uploadImage(imageUri: Uri,
                    collection: String,
                    resultListener: (String) -> Unit) {
        Log.d(TAG, "uploadImage: ${imageUri}")
        val uuid = UUID.randomUUID().toString()
        val photoRef = photoStorage.child("${collection}/${uuid}.jpg")
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()
        val uploadTask = photoRef.putFile(imageUri, metadata)
        uploadTask
            .addOnFailureListener {
                Log.d(TAG, "Upload FAILED $uuid")
            }
            .addOnSuccessListener {
                Log.d(TAG, "Upload succeeded $uuid")
                resultListener(uuid)
            }
    }
}