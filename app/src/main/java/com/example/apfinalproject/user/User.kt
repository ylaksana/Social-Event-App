package com.example.apfinalproject.user

class User(private val nullableName: String? = null,
           private val nullableEmail: String? = null,
           val uid: String = "-1") {
    val displayName: String = nullableName ?: "User logged out"
    val email: String = nullableEmail ?: "User logged out"

    val first_name: String = ""
    val last_name: String = ""
    var bio: String = "Wow... nothing's here yet!"
    var profile_image: String = ""

    // These need to be lists of strings because Firestore can't
    // unpack livedata or other objects
    var userInterests: List<String> = listOf()
    var pastEvents: List<String> = listOf()

    fun isInvalid(): Boolean {
        return this.uid == invalidUserUid
    }

//    fun addInterest(interest: Interest) {
//        if (!userInterests.contains(interest)) {
//            // Make sure that the interest is a valid interest and
//            // that the user does not already have it
//            userInterests.add(interest)
//        }
//    }
//    fun removeInterest(interest: Interest) {
//        userInterests.remove(interest)
//    }
//    fun addPastEvent(event: Event) {
//        pastEvents.value?.add(event)
//    }
//    fun removePastEvent(event: Event) {
//        pastEvents.value?.remove(event)
//    }
}
const val invalidUserUid = "-1"
val invalidUser = User(null, null, invalidUserUid)
