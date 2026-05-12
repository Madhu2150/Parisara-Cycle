package com.parisara.cycle.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.parisara.cycle.data.model.HazardPin
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val hazardCollection = firestore.collection("hazard_pins")

    suspend fun addHazardPin(pin: HazardPin): Result<String> = runCatching {
        val doc = hazardCollection.add(pin.toMap()).await()
        // Update the doc with its own ID
        hazardCollection.document(doc.id).update("id", doc.id).await()
        doc.id
    }

    fun getHazardPinsFlow(): Flow<List<HazardPin>> = callbackFlow {
        val listener = hazardCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(200)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val pins = snapshot?.documents?.mapNotNull { doc ->
                    @Suppress("UNCHECKED_CAST")
                    (doc.data as? Map<String, Any>)?.let {
                        HazardPin.fromMap(doc.id, it)
                    }
                } ?: emptyList()
                trySend(pins)
            }
        awaitClose { listener.remove() }
    }

    suspend fun upvoteHazard(pinId: String): Result<Unit> = runCatching {
        firestore.runTransaction { tx ->
            val ref = hazardCollection.document(pinId)
            val current = tx.get(ref).getLong("upvotes") ?: 0
            tx.update(ref, "upvotes", current + 1)
        }.await()
    }

    suspend fun deleteHazardPin(pinId: String): Result<Unit> = runCatching {
        hazardCollection.document(pinId).delete().await()
    }
}
