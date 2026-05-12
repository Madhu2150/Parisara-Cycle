package com.parisara.cycle.data.model

data class RouteInfo(
    val distanceKm: Double,
    val durationMinutes: Int,
    val co2SavedGrams: Int,
    val routeId: String,
    val polylinePoints: String,
    val aiSummary: String = "",
    val waypoints: List<LatLng> = emptyList()
) {
    data class LatLng(val lat: Double, val lng: Double)
}