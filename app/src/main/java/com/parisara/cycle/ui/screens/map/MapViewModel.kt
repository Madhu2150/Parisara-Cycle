package com.parisara.cycle.ui.screens.map

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.parisara.cycle.data.model.HazardPin
import com.parisara.cycle.data.model.RouteInfo
import com.parisara.cycle.data.remote.GeminiService
import com.parisara.cycle.data.repository.EcoStatsRepository
import com.parisara.cycle.data.repository.HazardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

data class MapUiState(
    val userLocation: GeoPoint? = null,
    val destination: GeoPoint? = null,
    val routeInfo: RouteInfo? = null,
    val hazardPins: List<HazardPin> = emptyList(),
    val showHazards: Boolean = true,
    val isLoadingAi: Boolean = false,
    val errorMessage: String? = null,
    val aiSummary: String = ""
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val hazardRepository: HazardRepository,
    private val ecoStatsRepository: EcoStatsRepository,
    private val geminiService: GeminiService,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init { observeHazards() }

    private fun observeHazards() {
        viewModelScope.launch {
            hazardRepository.getHazardPins()
                .catch {
                    _uiState.update {
                        it.copy(errorMessage = "Could not load hazards")
                    }
                }
                .collect { pins ->
                    _uiState.update { it.copy(hazardPins = pins) }
                }
        }
    }

    fun updateUserLocation(location: Location) {
        _uiState.update {
            it.copy(
                userLocation = GeoPoint(location.latitude, location.longitude)
            )
        }
    }

    fun setDestination(point: GeoPoint) {
        _uiState.update { it.copy(destination = point) }
        generateAiSummary(5.0, 20)
    }

    fun toggleHazards() {
        _uiState.update { it.copy(showHazards = !it.showHazards) }
    }

    private fun generateAiSummary(distanceKm: Double, durationMinutes: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingAi = true) }
            geminiService.generateRouteSummary(
                distanceKm      = distanceKm,
                durationMinutes = durationMinutes,
                hazardsOnRoute  = _uiState.value.hazardPins.size,
                startName       = "Your Location",
                endName         = "Destination"
            ).onSuccess { summary ->
                _uiState.update {
                    it.copy(aiSummary = summary, isLoadingAi = false)
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoadingAi = false,
                        aiSummary   = "Stay safe on your green ride! 🚴"
                    )
                }
            }
        }
    }

    fun logRide(distanceKm: Double) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            ecoStatsRepository.logRide(userId, distanceKm)
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}