package com.parisara.cycle.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eco_stats")
data class EcoStatsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val dateKey: String,           // "YYYY-MM-DD"
    val monthKey: String,          // "YYYY-MM"
    val distanceKm: Double,
    val co2SavedGrams: Int,
    val timestamp: Long = System.currentTimeMillis()
)
