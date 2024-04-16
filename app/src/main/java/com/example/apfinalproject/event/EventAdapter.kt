package com.example.apfinalproject.event

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.apfinalproject.databinding.EventRowBinding
//import com.example.apfinalproject.api.ImageRepository
import com.example.apfinalproject.MainViewModel

class EventAdapter(private val viewModel: MainViewModel,
                   private val navigateToOneEvent: (Event) -> Unit)
    : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiff()) {

    inner class EventViewHolder(val eventRowBinding: EventRowBinding)
        : RecyclerView.ViewHolder(eventRowBinding.root) {
            init{
                itemView.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the RedditPost
                        val post = getItem(position)
                        // Go to OnePost
                        navigateToOneEvent(post)
                        viewModel.hideActionBar()
                    }
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val eventRowBinding = EventRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(eventRowBinding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val eventRowBinding = holder.eventRowBinding
        val event = getItem(position)

        eventRowBinding.eventTitle.text = event.title
        eventRowBinding.eventDate.text = event.date
        eventRowBinding.eventTime.text = event.time
        eventRowBinding.eventLocation.text = event.location
        eventRowBinding.eventDescription.text = event.description
        Log.d("EventAdapter", "fetching image: ${event.imageName}")
        viewModel.fetchEventImage(event.imageName, eventRowBinding.image)

    }

    class EventDiff : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.location == newItem.location &&
                    oldItem.title == newItem.title &&
                    oldItem.description == newItem.description &&
                    oldItem.date == newItem.date &&
                    oldItem.time == newItem.time &&
                    oldItem.creator == newItem.creator &&
                    oldItem.type == newItem.type &&
                    oldItem.imageName == newItem.imageName
        }

    }
}