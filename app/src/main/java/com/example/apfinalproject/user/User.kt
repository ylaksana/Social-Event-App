package com.example.apfinalproject.user

class User(private val nullableName: String? = null,
           private val nullableEmail: String? = null,
           val uid: String = "-1") {
    val displayName: String = nullableName ?: "User logged out"
    val email: String = nullableEmail ?: "User logged out"

    var firstName: String = ""
    var lastName: String = ""
    var bio: String = "Wow... nothing's here yet!"
    var profileImage: String = ""
    var conversationIDs: List<String> = listOf()

    // These need to be lists of strings because Firestore can't
    // unpack livedata or other objects
    var userInterests: List<String> = listOf()
    var pastEvents: List<String> = listOf() // TODO: Switch to created Events in the past

    fun isInvalid(): Boolean {
        return this.uid == invalidUserUid
    }
    fun copy(
        firstName: String = this.firstName,
        lastName: String = this.lastName,
        bio: String = this.bio,
        profileImage: String = this.profileImage,
        userInterests: List<String> = this.userInterests,
        pastEvents: List<String> = this.pastEvents,
        conversationIDs: List<String> = this.conversationIDs
    ): User {
        val newUser = User(this.displayName, this.email, this.uid)
        newUser.firstName = firstName
        newUser.lastName = lastName
        newUser.bio = bio
        newUser.profileImage = profileImage
        newUser.userInterests = userInterests
        newUser.pastEvents = pastEvents
        newUser.conversationIDs = conversationIDs
        return newUser
    }

}
const val invalidUserUid = "-1"
val invalidUser = User(null, null, invalidUserUid)
