package com.example.apfinalproject.osm

import com.google.gson.annotations.SerializedName

data class OSMLocation(
    @SerializedName("place_id")
    val placeId: Long,
    @SerializedName("lat")
    val latitude: String,
    @SerializedName("lon")
    val longitude: String,
    @SerializedName("display_name")
    val displayName: String
)
