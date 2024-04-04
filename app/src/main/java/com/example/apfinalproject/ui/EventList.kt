// Contains mock data for the events to build the RecyclerView
// Code modeled after the RecyclerView FC

package com.example.apfinalproject.ui

object EventList {
    fun getAll(): List<Event> {return list}

    fun size(): Int {return list.size}

    private var list: List<Event> = listOf (
        Event("1", "Event 1", "Description 1", "Date 1", "Time 1", "Location 1", "Host 1"),
        Event("2", "Event 2", "Description 2", "Date 2", "Time 2", "Location 2", "Host 2"),
        Event("3", "Event 3", "Description 3", "Date 3", "Time 3", "Location 3", "Host 3"),
        Event("4", "Event 4", "Description 4", "Date 4", "Time 4", "Location 4", "Host 4"),
        Event("5", "Event 5", "Description 5", "Date 5", "Time 5", "Location 5", "Host 5"),
        Event("6", "Event 6", "Description 6", "Date 6", "Time 6", "Location 6", "Host 6"),
        Event("7", "Event 7", "Description 7", "Date 7", "Time 7", "Location 7", "Host 7"),
    )

}