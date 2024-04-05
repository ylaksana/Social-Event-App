// Contains mock data for the events to build the RecyclerView
// Code modeled after the RecyclerView FC

package com.example.apfinalproject.api

import com.example.apfinalproject.ui.Event

object EventList {
    fun getAll(): List<Event> {return list
    }

//    fun size(): Int {return list.size}

    private var list: List<Event> = listOf (
        Event("1", "Austin City Limits", "Description 1", "Date 1", "Time 1", "Location 1", "Host 1", "image1"),
        Event("2", "Barton Springs", "Description 2", "Date 2", "Time 2", "Location 2", "Host 2", "image2"),
        Event("3", "Bats", "Description 3", "Date 3", "Time 3", "Location 3", "Host 3", "image3"),
        Event("4", "La Barbecue", "Description 4", "Date 4", "Time 4", "Location 4", "Host 4", "image4"),
        Event("5", "Circuit of the Americas", "Description 5", "Date 5", "Time 5", "Location 5", "Host 5", "image5"),
        Event("6", "The Bats!", "Description 6", "Date 6", "Time 6", "Location 6", "Host 6", "image6"),
    )

}