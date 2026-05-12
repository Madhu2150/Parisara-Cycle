package com.parisara.cycle.util

object GeoHashUtil {
    private const val BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz"

    fun encode(lat: Double, lon: Double, precision: Int = 6): String {
        var minLat = -90.0; var maxLat = 90.0
        var minLon = -180.0; var maxLon = 180.0
        val sb = StringBuilder()
        var isEven = true
        var bit = 0
        var ch = 0

        while (sb.length < precision) {
            if (isEven) {
                val mid = (minLon + maxLon) / 2
                if (lon >= mid) { ch = ch or (1 shl (4 - bit)); minLon = mid }
                else maxLon = mid
            } else {
                val mid = (minLat + maxLat) / 2
                if (lat >= mid) { ch = ch or (1 shl (4 - bit)); minLat = mid }
                else maxLat = mid
            }
            isEven = !isEven
            if (bit < 4) {
                bit++
            } else {
                sb.append(BASE32[ch])
                bit = 0; ch = 0
            }
        }
        return sb.toString()
    }
}