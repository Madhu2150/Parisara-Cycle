package com.parisara.cycle.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.parisara.cycle.data.model.BuddyLocation
import com.parisara.cycle.util.GeoHashUtil
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuddyRepository @Inject constructor(
    private val realtimeDb: FirebaseDatabase
) {
    private val buddiesRef = realtimeDb.getReference("active_buddies")

    suspend fun broadcastLocation(buddy: BuddyLocation) {
        buddiesRef.child(buddy.userId).setValue(buddy).await()
    }

    suspend fun stopBroadcasting(userId: String) {
        buddiesRef.child(userId).child("isActive").setValue(false).await()
    }

    suspend fun removeFromBuddySystem(userId: String) {
        buddiesRef.child(userId).removeValue().await()
    }

    fun getBuddiesOnRoute(routeId: String): Flow<List<BuddyLocation>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val buddies = mutableListOf<BuddyLocation>()
                for (child in snapshot.children) {
                    val buddy = child.getValue(BuddyLocation::class.java)
                    if (buddy != null && buddy.routeId == routeId && buddy.isActive) {
                        buddies.add(buddy)
                    }
                }
                trySend(buddies)
            }

            // ✅ Fixed: return type is Unit (no close())
            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        buddiesRef.addValueEventListener(listener)
        awaitClose { buddiesRef.removeEventListener(listener) }
    }

    fun getAllActiveBuddies(): Flow<List<BuddyLocation>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val buddies = snapshot.children.mapNotNull {
                    it.getValue(BuddyLocation::class.java)
                }.filter { it.isActive }
                trySend(buddies)
            }

            // ✅ Fixed: return type is Unit (no close())
            override fun onCancelled(error: DatabaseError) {
                trySend(emptyList())
            }
        }
        buddiesRef.addValueEventListener(listener)
        awaitClose { buddiesRef.removeEventListener(listener) }
    }
}