package com.parisara.cycle.data.model

data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val totalRides: Int = 0,
    val totalCo2Saved: Int = 0,
    val totalDistanceKm: Double = 0.0,
    val joinedDate: String = "",
    val favouriteRoute: String = ""
)