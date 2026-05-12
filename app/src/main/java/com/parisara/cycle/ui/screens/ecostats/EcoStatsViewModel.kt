package com.parisara.cycle.ui.screens.ecostats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.parisara.cycle.data.local.EcoStatsEntity
import com.parisara.cycle.data.repository.EcoStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EcoStatsUiState(
    val dailyCo2Grams: Int = 0,
    val monthlyCo2Grams: Int = 0,
    val monthlyDistanceKm: Double = 0.0,
    val totalCo2Grams: Int = 0,
    val monthlyRideCount: Int = 0,
    val recentRides: List<EcoStatsEntity> = emptyList(),
    val isLoading: Boolean = true,
    val inputDistanceKm: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class EcoStatsViewModel @Inject constructor(
    private val ecoStatsRepository: EcoStatsRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(EcoStatsUiState())
    val uiState: StateFlow<EcoStatsUiState> = _uiState.asStateFlow()

    // ✅ Use "anonymous" if not logged in — prevents crash
    private val userId: String
        get() = auth.currentUser?.uid ?: "anonymous"

    init {
        loadStats()
    }

    private fun loadStats() {
        val uid = userId
        viewModelScope.launch {
            try {
                combine(
                    ecoStatsRepository.getDailyCo2(uid),
                    ecoStatsRepository.getMonthlyCo2(uid),
                    ecoStatsRepository.getMonthlyDistance(uid),
                    ecoStatsRepository.getTotalCo2(uid),
                    ecoStatsRepository.getMonthlyRideCount(uid)
                ) { daily, monthly, distance, total, count ->
                    EcoStatsUiState(
                        dailyCo2Grams     = daily,
                        monthlyCo2Grams   = monthly,
                        monthlyDistanceKm = distance,
                        totalCo2Grams     = total,
                        monthlyRideCount  = count,
                        isLoading         = false
                    )
                }.catch { e ->
                    emit(EcoStatsUiState(
                        isLoading    = false,
                        errorMessage = e.message
                    ))
                }.collect { state ->
                    _uiState.update {
                        state.copy(
                            inputDistanceKm = it.inputDistanceKm,
                            recentRides     = it.recentRides
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message)
                }
            }
        }

        viewModelScope.launch {
            try {
                ecoStatsRepository.getRecentRides(uid).collect { rides ->
                    _uiState.update { it.copy(recentRides = rides) }
                }
            } catch (e: Exception) {
                // Silently ignore — recent rides not critical
            }
        }
    }

    fun setInputDistance(km: String) =
        _uiState.update { it.copy(inputDistanceKm = km) }

    fun logManualRide() {
        val km = _uiState.value.inputDistanceKm.toDoubleOrNull() ?: return
        if (km <= 0) return
        viewModelScope.launch {
            try {
                ecoStatsRepository.logRide(userId, km)
                _uiState.update { it.copy(inputDistanceKm = "") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to log ride") }
            }
        }
    }
}