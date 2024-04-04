package com.example.apfinalproject.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.apfinalproject.databinding.EventRowBinding

class EventAdapter(private val viewModel: MainViewModel,
                   private val navigateToOneEvent: (Event) -> Unit)
    : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiff()) {
    inner class EventViewHolder(val eventRowBinding: EventRowBinding)
        : RecyclerView.ViewHolder(eventRowBinding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val eventRowBinding = EventRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(eventRowBinding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val eventRowBinding = holder.eventRowBinding
        val event = getItem(position)
        eventRowBinding.eventTitle.text = event.getEventTitle()
        eventRowBinding.eventDate.text = event.getEventDate()
        eventRowBinding.eventTime.text = event.getEventTime()
        eventRowBinding.eventLocation.text = event.getEventLocation()
        eventRowBinding.eventDescription.text = event.getEventDescription()
    }

    class EventDiff : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.getEventID() == newItem.getEventID()
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.getEventLocation() == newItem.getEventLocation() &&
                    oldItem.getEventTitle() == newItem.getEventTitle() &&
                    oldItem.getEventDescription() == newItem.getEventDescription() &&
                    oldItem.getEventDate() == newItem.getEventDate() &&
                    oldItem.getEventTime() == newItem.getEventTime() &&
                    oldItem.getEventCreator() == newItem.getEventCreator()
        }

    }
}