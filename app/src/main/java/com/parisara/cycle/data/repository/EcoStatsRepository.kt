package com.parisara.cycle.data.repository

import com.parisara.cycle.data.local.AppDatabase
import com.parisara.cycle.data.local.EcoStatsEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EcoStatsRepository @Inject constructor(
    private val db: AppDatabase
) {
    private val dao = db.ecoStatsDao()
    private val sdfDate  = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val sdfMonth = SimpleDateFormat("yyyy-MM",   Locale.getDefault())

    suspend fun logRide(userId: String, distanceKm: Double) {
        val now       = Date()
        val co2Saved  = (distanceKm * 120).toInt()
        dao.insertRide(
            EcoStatsEntity(
                userId       = userId,
                dateKey      = sdfDate.format(now),
                monthKey     = sdfMonth.format(now),
                distanceKm   = distanceKm,
                co2SavedGrams = co2Saved
            )
        )
    }

    fun getDailyCo2(userId: String): Flow<Int> {
        val today = sdfDate.format(Date())
        return dao.getDailyCo2(userId, today)
    }

    fun getMonthlyCo2(userId: String): Flow<Int> {
        val month = sdfMonth.format(Date())
        return dao.getMonthlyCo2(userId, month)
    }

    fun getMonthlyDistance(userId: String): Flow<Double> {
        val month = sdfMonth.format(Date())
        return dao.getMonthlyDistance(userId, month)
    }

    fun getTotalCo2(userId: String): Flow<Int> = dao.getTotalCo2(userId)

    fun getMonthlyRideCount(userId: String): Flow<Int> {
        val month = sdfMonth.format(Date())
        return dao.getRideCountThisMonth(userId, month)
    }

    fun getRecentRides(userId: String) = dao.getRecentRides(userId)

    companion object {
        /** 1 km cycling = 120 g CO2 saved vs average car */
        fun calculateCo2(distanceKm: Double): Int = (distanceKm * 120).toInt()
    }
}