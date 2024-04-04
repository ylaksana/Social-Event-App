package com.example.apfinalproject.ui

public class Event(
    private val id: String,
    private var title: String,
    private var description: String,
    private var date: String,
    private var time: String,
    private var location: String,
    private val creator: String) {
    public fun getEventID(): String {
        return this.id
    }
    public fun getEventTitle(): String {
        return this.title
    }
    public fun getEventDescription(): String {
        return this.description
    }
    public fun getEventDate(): String {
        return this.date
    }
    public fun getEventTime(): String {
        return this.time
    }
    public fun getEventLocation(): String {
        return this.location
    }
    public fun getEventCreator(): String {
        return this.creator
    }

    // Events with the same ID are considered equal.
    // Not sure if this is the best way to do it, but it works for now.
    override fun equals(other: Any?): Boolean =
        if (other is Event) {
            id == other.id
        } else {
            false
        }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + creator.hashCode()
        return result
    }
}