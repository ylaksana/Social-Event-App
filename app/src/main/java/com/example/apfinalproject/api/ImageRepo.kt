package com.example.apfinalproject.api

import android.graphics.BitmapFactory
import android.graphics.Bitmap
//import com.example.apfinalproject.MainActivity
import android.content.Context

class ImageRepository(private val context: Context?) {
    fun get(imageName: String): Bitmap {
        val assetManager = context?.assets
        val inputStream = assetManager?.open("$imageName.jpg")
        return BitmapFactory.decodeStream(inputStream)
    }
}