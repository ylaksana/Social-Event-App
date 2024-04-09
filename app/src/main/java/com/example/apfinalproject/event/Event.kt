package com.example.apfinalproject.event
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Event(
    val uid: String = "-1",
    var title: String = "No Title",
    var description: String = "No Description",
    var date: String = "No Date",
    var time: String = "No Time",
    var location: String = "No Location",
    val creator: String = "No Creator",
    val imageName: String = "No Image") : Parcelable{

    fun getEventID(): String {
        return this.uid
    }
    fun getEventTitle(): String {
        return this.title
    }
    fun getEventDescription(): String {
        return this.description
    }
    fun getEventDate(): String {
        return this.date
    }
    fun getEventTime(): String {
        return this.time
    }
    fun getEventLocation(): String {
        return this.location
    }
    fun getEventCreator(): String {
        return this.creator
    }
    fun getEventImageName(): String {
        return this.imageName
    }

    // Events with the same ID are considered equal.
    // Not sure if this is the best way to do it, but it works for now.
    override fun equals(other: Any?): Boolean =
        if (other is Event) {
            uid == other.uid
        } else {
            false
        }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + creator.hashCode()
        return result
    }
}