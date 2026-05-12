package com.parisara.cycle.ui.screens.buddy

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.parisara.cycle.data.model.BuddyLocation
import com.parisara.cycle.data.repository.BuddyRepository
import com.parisara.cycle.util.GeoHashUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

data class BuddyUiState(
    val isBuddyModeActive: Boolean = false,
    val currentRouteId: String = "",
    val buddies: List<BuddyLocation> = emptyList(),
    val myLocation: GeoPoint? = null,
    val error: String? = null
)

@HiltViewModel
class BuddyViewModel @Inject constructor(
    private val buddyRepository: BuddyRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuddyUiState())
    val uiState: StateFlow<BuddyUiState> = _uiState.asStateFlow()

    private var broadcastJob: Job? = null
    private var observeJob: Job?   = null
    private var currentLocation: Location? = null

    fun updateLocation(location: Location) {
        currentLocation = location
        _uiState.update {
            it.copy(myLocation = GeoPoint(location.latitude, location.longitude))
        }
    }

    fun startBuddyMode(routeId: String) {
        val userId = auth.currentUser?.uid
            ?: "anonymous_${System.currentTimeMillis()}"
        val displayName = auth.currentUser?.displayName
            ?: auth.currentUser?.email?.substringBefore("@")
            ?: "Cyclist"

        _uiState.update {
            it.copy(isBuddyModeActive = true, currentRouteId = routeId, error = null)
        }

        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            try {
                buddyRepository.getBuddiesOnRoute(routeId)
                    .catch { e -> _uiState.update { it.copy(error = e.message) } }
                    .collect { buddies ->
                        _uiState.update { state ->
                            state.copy(buddies = buddies.filter { it.userId != userId })
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Could not connect to buddy service") }
            }
        }

        broadcastJob?.cancel()
        broadcastJob = viewModelScope.launch {
            while (isActive) {
                try {
                    currentLocation?.let { loc ->
                        val buddy = BuddyLocation(
                            userId      = userId,
                            displayName = displayName,
                            latitude    = loc.latitude,
                            longitude   = loc.longitude,
                            routeId     = routeId,
                            geohash     = GeoHashUtil.encode(
                                loc.latitude, loc.longitude
                            ),
                            lastUpdated = System.currentTimeMillis(),
                            isActive    = true
                        )
                        buddyRepository.broadcastLocation(buddy)
                    }
                } catch (e: Exception) { /* silent retry */ }
                delay(10_000L)
            }
        }
    }

    fun stopBuddyMode() {
        broadcastJob?.cancel()
        broadcastJob = null
        observeJob?.cancel()
        observeJob = null

        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                buddyRepository.stopBroadcasting(userId)
            } catch (e: Exception) { }
        }

        _uiState.update {
            it.copy(isBuddyModeActive = false, buddies = emptyList())
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopBuddyMode()
    }
}