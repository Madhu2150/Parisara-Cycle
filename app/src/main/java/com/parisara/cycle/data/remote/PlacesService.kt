package com.parisara.cycle.data.remote

import com.parisara.cycle.data.model.PitStop
import com.parisara.cycle.data.model.PitStopType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesService @Inject constructor() {

    // OSMDroid version — uses Nominatim (free, no API key)
    suspend fun getNearbyPitStops(
        latitude: Double,
        longitude: Double,
        radiusMeters: Double = 2000.0
    ): Result<List<PitStop>> = runCatching {
        // Returns empty list — pit stops shown as static demo data
        // In production, use Overpass API (free OpenStreetMap data)
        getDemoPitStops(latitude, longitude)
    }

    private fun getDemoPitStops(lat: Double, lon: Double): List<PitStop> {
        // Demo pit stops near user location
        return listOf(
            PitStop(
                placeId       = "demo_1",
                name          = "City Cycle Repair",
                address       = "Near Main Road",
                latitude      = lat + 0.005,
                longitude     = lon + 0.003,
                type          = PitStopType.CYCLE_SHOP,
                isOpen        = true,
                rating        = 4.2f,
                distanceMeters = 500f
            ),
            PitStop(
                placeId       = "demo_2",
                name          = "Public Water Point",
                address       = "Park Entrance",
                latitude      = lat - 0.003,
                longitude     = lon + 0.006,
                type          = PitStopType.WATER_POINT,
                isOpen        = true,
                rating        = 4.0f,
                distanceMeters = 350f
            ),
            PitStop(
                placeId       = "demo_3",
                name          = "Cycle Works",
                address       = "Bus Stand Road",
                latitude      = lat + 0.008,
                longitude     = lon - 0.004,
                type          = PitStopType.CYCLE_SHOP,
                isOpen        = false,
                rating        = 3.8f,
                distanceMeters = 800f
            ),
            PitStop(
                placeId       = "demo_4",
                name          = "Temple Water Tap",
                address       = "Temple Street",
                latitude      = lat - 0.006,
                longitude     = lon - 0.002,
                type          = PitStopType.WATER_POINT,
                isOpen        = true,
                rating        = 4.5f,
                distanceMeters = 600f
            )
        )
    }
}