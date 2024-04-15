package com.example.apfinalproject.interest

object InterestCategories {
    data class Interest(
        val category: String,
        val subcategory: String
    )

//    fun getInterests(): MutableMap<String, MutableList<Interest>> {
//        return interestCategories
//    }

//    private val interestCategories: MutableMap<String, MutableList<Interest>> = mutableMapOf(
//        "Sports" to mutableListOf(
//            Interest("Sports", "Basketball"),
//            Interest("Sports", "Soccer"),
//            Interest("Sports", "Football"),
//            Interest("Sports", "Baseball"),
//            Interest("Sports", "Tennis"),
//            Interest("Sports", "Golf"),
//            Interest("Sports", "Swimming"),
//            Interest("Sports", "Running"),
//            Interest("Sports", "Cycling")),
//        "Music" to mutableListOf(
//            Interest("Music", "Rock"),
//            Interest("Music", "Pop"),
//            Interest("Music", "Rap"),
//            Interest("Music", "Country"),
//            Interest("Music", "Jazz"),
//            Interest("Music", "Classical"),
//            Interest("Music", "Electronic"),
//            Interest("Music", "Reggae"),
//            Interest("Music", "Blues")),
//        "Food" to mutableListOf(
//            Interest("Food", "Italian"),
//            Interest("Food", "Mexican"),
//            Interest("Food", "Chinese"),
//            Interest("Food", "Japanese"),
//            Interest("Food", "Indian"),
//            Interest("Food", "Thai"),
//            Interest("Food", "American"),
//            Interest("Food", "French"),
//            Interest("Food", "Mediterranean")),
//        "Movies" to mutableListOf(
//            Interest("Movies", "Action"),
//            Interest("Movies", "Comedy"),
//            Interest("Movies", "Drama"),
//            Interest("Movies", "Horror"),
//            Interest("Movies", "Romance"),
//            Interest("Movies", "Sci-Fi"),
//            Interest("Movies", "Thriller"),
//            Interest("Movies", "Documentary"),
//            Interest("Movies", "Animation")),
//        "Books" to mutableListOf(
//            Interest("Books", "Fiction"),
//            Interest("Books", "Non-Fiction"),
//            Interest("Books", "Mystery"),
//            Interest("Books", "Fantasy"),
//            Interest("Books", "Science Fiction"),
//            Interest("Books", "Biography"),
//            Interest("Books", "Self-Help"),
//            Interest("Books", "History"),
//            Interest("Books", "Romance")),
//        "Travel" to mutableListOf(
//            Interest("Travel", "Domestic"),
//            Interest("Travel", "International"),
//            Interest("Travel", "Beach"),
//            Interest("Travel", "Mountains"),
//            Interest("Travel", "City"),
//            Interest("Travel", "Camping")),
//        "Art" to mutableListOf(
//            Interest("Art", "Painting"),
//            Interest("Art", "Sculpture"),
//            Interest("Art", "Photography"),
//            Interest("Art", "Drawing"),
//            Interest("Art", "Digital"),
//            Interest("Art", "Mixed Media"),
//            Interest("Art", "Printmaking"),
//            Interest("Art", "Ceramics"),
//            Interest("Art", "Textiles")),
//        "Technology" to mutableListOf(
//            Interest("Technology", "Programming"),
//            Interest("Technology", "Hardware"),
//            Interest("Technology", "Software"),
//            Interest("Technology", "Networking"),
//            Interest("Technology", "Cybersecurity"),
//            Interest("Technology", "Artificial Intelligence"),
//            Interest("Technology", "Machine Learning"),
//            Interest("Technology", "Data Science"),
//            Interest("Technology", "Web Development")),
//        "Gaming" to mutableListOf(
//            Interest("Gaming", "PC"),
//            Interest("Gaming", "Console"),
//            Interest("Gaming", "Mobile"),
//            Interest("Gaming", "VR"),
//            Interest("Gaming", "Board Games"),
//            Interest("Gaming", "Card Games"),
//            Interest("Gaming", "RPG"),
//            Interest("Gaming", "Strategy"),
//            Interest("Gaming", "Puzzle"))
//    )

    private val eventCategories: List<String> = listOf(
        "Business",
        "Food & Drink",
        "Health",
        "Music",
        "Auto, Boat & Air",
        "Charity & Causes",
        "Community",
        "Family & Education",
        "Fashion",
        "Film & Media",
        "Hobbies",
        "Home & Lifestyle",
        "Performing & Visual Arts",
        "Government",
        "Spirituality",
        "School Activities",
        "Science & Tech",
        "Holidays",
        "Sports & Fitness",
        "Travel & Outdoor",
        "Other"
    )
}