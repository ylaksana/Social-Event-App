package com.example.apfinalproject

import java.util.UUID
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

class PhotoSelectorWrapper {
    companion object {
        fun generateFileName(): String {
            // This is where we would generate a unique filename
            return UUID.randomUUID().toString()
        }

        fun selectPhoto(selectPhotoLauncher: ActivityResultLauncher<String>) {
            // This is where we would select a photo from the gallery
            val type = "image/*"
            selectPhotoLauncher.launch(type)
        }
    }
}