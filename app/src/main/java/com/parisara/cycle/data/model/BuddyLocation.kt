package com.parisara.cycle.data.model

data class BuddyLocation(
    val userId: String = "",
    val displayName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val routeId: String = "",
    val geohash: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)