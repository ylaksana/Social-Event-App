package com.example.apfinalproject.interest

object InterestCategories {
    data class Interest(
        val category: String,
        val subcategory: String
    )

    fun getInterests(): List<String> {
        return eventCategories
    }

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