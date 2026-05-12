package com.parisara.cycle.data.repository

import com.parisara.cycle.data.model.HazardPin
import com.parisara.cycle.data.remote.FirestoreService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HazardRepository @Inject constructor(
    private val firestoreService: FirestoreService
) {
    fun getHazardPins(): Flow<List<HazardPin>> = firestoreService.getHazardPinsFlow()

    suspend fun reportHazard(pin: HazardPin): Result<String> =
        firestoreService.addHazardPin(pin)

    suspend fun upvoteHazard(pinId: String): Result<Unit> =
        firestoreService.upvoteHazard(pinId)
}