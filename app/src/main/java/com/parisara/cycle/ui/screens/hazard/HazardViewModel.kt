package com.parisara.cycle.ui.screens.hazard

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.parisara.cycle.data.model.HazardCategory
import com.parisara.cycle.data.model.HazardPin
import com.parisara.cycle.data.repository.HazardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

data class HazardReportState(
    val selectedCategory: HazardCategory = HazardCategory.POTHOLE,
    val description: String = "",
    val photoUri: Uri? = null,
    val pinnedLocation: GeoPoint? = null,
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HazardViewModel @Inject constructor(
    private val hazardRepository: HazardRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(HazardReportState())
    val state: StateFlow<HazardReportState> = _state.asStateFlow()

    val hazardPins = hazardRepository.getHazardPins()
        .catch { emit(emptyList()) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun setCategory(category: HazardCategory) =
        _state.update { it.copy(selectedCategory = category) }

    fun setDescription(desc: String) =
        _state.update { it.copy(description = desc) }

    fun setPhotoUri(uri: Uri?) =
        _state.update { it.copy(photoUri = uri) }

    fun setPinnedLocation(point: GeoPoint) =
        _state.update { it.copy(pinnedLocation = point) }

    fun submitHazard() {
        val currentState = _state.value
        val location     = currentState.pinnedLocation ?: return
        val userId       = auth.currentUser?.uid ?: "anonymous"

        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }
            try {
                val pin = HazardPin(
                    latitude    = location.latitude,
                    longitude   = location.longitude,
                    category    = currentState.selectedCategory.name,
                    description = currentState.description,
                    reportedBy  = userId,
                    timestamp   = Timestamp.now()
                )
                hazardRepository.reportHazard(pin)
                    .onSuccess {
                        _state.update {
                            it.copy(isSubmitting = false, isSuccess = true)
                        }
                    }
                    .onFailure { e ->
                        _state.update {
                            it.copy(isSubmitting = false, error = e.message)
                        }
                    }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isSubmitting = false, error = e.message)
                }
            }
        }
    }

    fun upvoteHazard(pinId: String) {
        viewModelScope.launch {
            try {
                hazardRepository.upvoteHazard(pinId)
            } catch (e: Exception) { }
        }
    }

    fun resetState() = _state.update { HazardReportState() }
}