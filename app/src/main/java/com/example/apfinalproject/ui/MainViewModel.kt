package com.example.apfinalproject.ui

import androidx.lifecycle.ViewModel
import com.example.apfinalproject.api.EventList
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(): ViewModel() {
    private var events: List<Event> = EventList.getAll()

    fun observeEvents(): List<Event> {
        return events
    }
}