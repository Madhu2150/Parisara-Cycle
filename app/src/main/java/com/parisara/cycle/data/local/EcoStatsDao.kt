package com.parisara.cycle.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EcoStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRide(stat: EcoStatsEntity)

    @Query("""
        SELECT COALESCE(SUM(co2SavedGrams), 0) 
        FROM eco_stats 
        WHERE userId = :userId AND dateKey = :dateKey
    """)
    fun getDailyCo2(userId: String, dateKey: String): Flow<Int>

    @Query("""
        SELECT COALESCE(SUM(co2SavedGrams), 0) 
        FROM eco_stats 
        WHERE userId = :userId AND monthKey = :monthKey
    """)
    fun getMonthlyCo2(userId: String, monthKey: String): Flow<Int>

    @Query("""
        SELECT COALESCE(SUM(distanceKm), 0.0) 
        FROM eco_stats 
        WHERE userId = :userId AND monthKey = :monthKey
    """)
    fun getMonthlyDistance(userId: String, monthKey: String): Flow<Double>

    @Query("""
        SELECT COALESCE(SUM(co2SavedGrams), 0) 
        FROM eco_stats 
        WHERE userId = :userId
    """)
    fun getTotalCo2(userId: String): Flow<Int>

    @Query("""
        SELECT COUNT(*) 
        FROM eco_stats 
        WHERE userId = :userId AND monthKey = :monthKey
    """)
    fun getRideCountThisMonth(userId: String, monthKey: String): Flow<Int>

    @Query("SELECT * FROM eco_stats WHERE userId = :userId ORDER BY timestamp DESC LIMIT 10")
    fun getRecentRides(userId: String): Flow<List<EcoStatsEntity>>
}