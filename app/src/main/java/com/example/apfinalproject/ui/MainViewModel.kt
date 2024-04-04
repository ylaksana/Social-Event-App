package com.example.apfinalproject.ui

import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private var events: List<Event> = EventList.getAll()

    fun observeEvents(): List<Event> {
        return events
    }
}