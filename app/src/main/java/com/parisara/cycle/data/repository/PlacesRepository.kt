package com.parisara.cycle.data.repository

import com.parisara.cycle.data.model.PitStop
import com.parisara.cycle.data.remote.PlacesService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesRepository @Inject constructor(
    private val placesService: PlacesService
) {
    suspend fun getNearbyPitStops(
        latitude: Double,
        longitude: Double
    ): Result<List<PitStop>> =
        placesService.getNearbyPitStops(latitude, longitude)
}
