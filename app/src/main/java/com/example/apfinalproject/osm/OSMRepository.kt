package com.example.apfinalproject.osm

import com.example.apfinalproject.event.Event

class OSMRepository(private val api: OSMApi) {
    suspend fun fetchLocations(eventList: List<Event>): List<OSMLocation> {
        val api = OSMApi.create()
        return eventList.map { event ->
            api.search(event.location).firstOrNull() ?: OSMLocation(0, 0.0, 0.0, "null") // Provide a default OSMLocation here
        }
    }

    suspend fun fetchLocation(location: String): OSMLocation {
        val api = OSMApi.create()
        return api.search(location).firstOrNull() ?: OSMLocation(0, 0.0, 0.0, "null") // Provide a default OSMLocation here
    }
}
