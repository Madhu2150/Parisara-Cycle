package com.parisara.cycle.data.model

import com.google.firebase.Timestamp

enum class HazardCategory(val label: String, val emoji: String) {
    POTHOLE("Pothole", "🕳️"),
    BLOCKED_PATH("Blocked Path", "🚧"),
    DANGEROUS_INTERSECTION("Dangerous Intersection", "⚠️")
}

data class HazardPin(
    val id: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val category: String = HazardCategory.POTHOLE.name,
    val description: String = "",
    val photoUrl: String = "",
    val reportedBy: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val upvotes: Int = 0
) {
    fun toMap(): Map<String, Any> = mapOf(
        "id"          to id,
        "latitude"    to latitude,
        "longitude"   to longitude,
        "category"    to category,
        "description" to description,
        "photoUrl"    to photoUrl,
        "reportedBy"  to reportedBy,
        "timestamp"   to timestamp,
        "upvotes"     to upvotes
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any>): HazardPin = HazardPin(
            id          = id,
            latitude    = (map["latitude"]  as? Double) ?: 0.0,
            longitude   = (map["longitude"] as? Double) ?: 0.0,
            category    = (map["category"]  as? String) ?: HazardCategory.POTHOLE.name,
            description = (map["description"] as? String) ?: "",
            photoUrl    = (map["photoUrl"]  as? String) ?: "",
            reportedBy  = (map["reportedBy"] as? String) ?: "",
            timestamp   = (map["timestamp"] as? Timestamp) ?: Timestamp.now(),
            upvotes     = ((map["upvotes"] as? Long) ?: 0L).toInt()
        )
    }
}