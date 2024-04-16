package com.example.apfinalproject.event
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Event(
    var uid: String = "-1",
    var title: String = "No Title",
    var description: String = "No Description",
    var date: String = "No Date",
    var time: String = "No Time",
    var location: String = "No Location",
    var creator: String = "No Creator",
    var type: String = "No Type",
    var imageName: String = "default.jpg") : Parcelable{

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