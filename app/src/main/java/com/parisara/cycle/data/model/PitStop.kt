package com.parisara.cycle.data.model

data class PitStop(
    val placeId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val type: PitStopType,
    val isOpen: Boolean,
    val rating: Float,
    val distanceMeters: Float
)

enum class PitStopType(val label: String, val emoji: String) {
    CYCLE_SHOP("Cycle Repair", "🔧"),
    WATER_POINT("Water Point", "💧")
}