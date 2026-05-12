package com.parisara.cycle.ui.screens.pitstop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parisara.cycle.data.model.PitStop
import com.parisara.cycle.data.repository.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PitStopUiState(
    val pitStops: List<PitStop> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PitStopViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PitStopUiState())
    val uiState: StateFlow<PitStopUiState> = _uiState.asStateFlow()

    fun loadPitStops(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            placesRepository.getNearbyPitStops(latitude, longitude)
                .onSuccess { stops ->
                    _uiState.update {
                        it.copy(pitStops = stops, isLoading = false)
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
        }
    }
}