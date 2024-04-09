package com.example.apfinalproject.event

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.apfinalproject.databinding.EventRowBinding
import com.example.apfinalproject.api.ImageRepository
import com.example.apfinalproject.ui.MainViewModel

class EventAdapter(private val viewModel: MainViewModel,
                   private val imageRepo: ImageRepository,
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
        eventRowBinding.eventTitle.text = event.getEventTitle()
        eventRowBinding.eventDate.text = event.getEventDate()
        eventRowBinding.eventTime.text = event.getEventTime()
        eventRowBinding.eventLocation.text = event.getEventLocation()
        eventRowBinding.eventDescription.text = event.getEventDescription()
//        eventRowBinding.image.setImageBitmap(imageRepo.get(event.getEventImageName()))
        viewModel.fetchEventImage(event.getEventImageName(), eventRowBinding.image)
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