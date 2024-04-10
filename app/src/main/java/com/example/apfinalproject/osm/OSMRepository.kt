package com.example.apfinalproject.osm

class OSMRepository (private val api: OSMApi){
    suspend fun fetchLocation(querySearch: String): List<OSMLocation> {
        return OSMApi.create().search(querySearch)
    }

}