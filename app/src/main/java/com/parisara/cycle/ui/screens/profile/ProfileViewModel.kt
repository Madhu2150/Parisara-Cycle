package com.parisara.cycle.ui.screens.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.parisara.cycle.data.model.UserProfile
import com.parisara.cycle.data.repository.EcoStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class ProfileUiState(
    val profile             : UserProfile = UserProfile(),
    val isLoading           : Boolean     = true,
    val isLoggedOut         : Boolean     = false,
    val showLogoutDialog    : Boolean     = false,
    val showImageOptions    : Boolean     = false,
    val showEditNameDialog  : Boolean     = false,
    val editNameValue       : String      = "",
    val isUpdatingName      : Boolean     = false,
    val isUpdatingPhoto     : Boolean     = false,
    val successMessage      : String?     = null,
    val errorMessage        : String?     = null,
    val totalCo2            : Int         = 0,
    val monthlyRides        : Int         = 0,
    val monthlyDistance     : Double      = 0.0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth               : FirebaseAuth,
    private val ecoStatsRepository : EcoStatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val storage = FirebaseStorage.getInstance()
    private val userId: String get() = auth.currentUser?.uid ?: ""

    init {
        loadProfile()
        loadStats()
    }

    // ─── Load ─────────────────────────────────────────────────

    private fun loadProfile() {
        val user = auth.currentUser ?: run {
            _uiState.update { it.copy(isLoading = false) }
            return
        }
        val sdf      = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val joinDate = user.metadata?.creationTimestamp
            ?.let { sdf.format(Date(it)) } ?: "Unknown"

        val profile = UserProfile(
            uid         = user.uid,
            displayName = user.displayName
                ?: user.email?.substringBefore("@")
                ?: "Cyclist",
            email       = user.email ?: "",
            photoUrl    = user.photoUrl?.toString() ?: "",
            joinedDate  = joinDate
        )
        _uiState.update {
            it.copy(
                profile        = profile,
                editNameValue  = profile.displayName,
                isLoading      = false
            )
        }
    }

    private fun loadStats() {
        val uid = userId
        if (uid.isEmpty()) return
        viewModelScope.launch {
            try {
                combine(
                    ecoStatsRepository.getTotalCo2(uid),
                    ecoStatsRepository.getMonthlyRideCount(uid),
                    ecoStatsRepository.getMonthlyDistance(uid)
                ) { total, rides, distance ->
                    Triple(total, rides, distance)
                }.catch { }.collect { (total, rides, distance) ->
                    _uiState.update {
                        it.copy(
                            totalCo2        = total,
                            monthlyRides    = rides,
                            monthlyDistance = distance
                        )
                    }
                }
            } catch (e: Exception) { }
        }
    }

    // ─── Name Editing ─────────────────────────────────────────

    fun showEditNameDialog() {
        _uiState.update {
            it.copy(
                showEditNameDialog = true,
                editNameValue      = it.profile.displayName
            )
        }
    }

    fun hideEditNameDialog() =
        _uiState.update { it.copy(showEditNameDialog = false) }

    fun setEditNameValue(name: String) =
        _uiState.update { it.copy(editNameValue = name) }

    fun updateDisplayName() {
        val newName = _uiState.value.editNameValue.trim()
        if (newName.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Name cannot be empty") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingName = true) }
            try {
                val request = UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build()
                auth.currentUser?.updateProfile(request)?.await()

                _uiState.update { state ->
                    state.copy(
                        isUpdatingName     = false,
                        showEditNameDialog = false,
                        profile            = state.profile.copy(displayName = newName),
                        successMessage     = "Name updated successfully! ✅"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUpdatingName = false,
                        errorMessage   = "Failed to update name: ${e.message}"
                    )
                }
            }
        }
    }

    // ─── Photo Options ────────────────────────────────────────

    fun showImageOptions() =
        _uiState.update { it.copy(showImageOptions = true) }

    fun hideImageOptions() =
        _uiState.update { it.copy(showImageOptions = false) }

    fun uploadProfilePhoto(uri: Uri) {
        val uid = userId
        if (uid.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingPhoto = true, showImageOptions = false) }
            try {
                // ✅ Removed .jpg — matches storage rules
                val ref      = storage.reference.child("profile_photos/$uid")
                ref.putFile(uri).await()
                val photoUrl = ref.downloadUrl.await().toString()

                val request = UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(photoUrl))
                    .build()
                auth.currentUser?.updateProfile(request)?.await()

                _uiState.update { state ->
                    state.copy(
                        isUpdatingPhoto = false,
                        profile         = state.profile.copy(photoUrl = photoUrl),
                        successMessage  = "Profile photo updated! ✅"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUpdatingPhoto = false,
                        errorMessage    = "Failed to upload photo: ${e.message}"
                    )
                }
            }
        }
    }

    // removeProfilePhoto function
    fun removeProfilePhoto() {
        val uid = userId
        if (uid.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingPhoto = true, showImageOptions = false) }
            try {
                try {
                    // ✅ Removed .jpg — matches storage rules
                    storage.reference
                        .child("profile_photos/$uid")
                        .delete()
                        .await()
                } catch (e: Exception) {
                    // File might not exist — ignore
                }

                val request = UserProfileChangeRequest.Builder()
                    .setPhotoUri(null)
                    .build()
                auth.currentUser?.updateProfile(request)?.await()

                _uiState.update { state ->
                    state.copy(
                        isUpdatingPhoto = false,
                        profile         = state.profile.copy(photoUrl = ""),
                        successMessage  = "Profile photo removed"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUpdatingPhoto = false,
                        errorMessage    = "Failed to remove photo: ${e.message}"
                    )
                }
            }
        }
    }

    // ─── Logout ───────────────────────────────────────────────

    fun showLogoutDialog() =
        _uiState.update { it.copy(showLogoutDialog = true) }

    fun hideLogoutDialog() =
        _uiState.update { it.copy(showLogoutDialog = false) }

    fun logout() {
        auth.signOut()
        _uiState.update { it.copy(isLoggedOut = true, showLogoutDialog = false) }
    }

    // ─── Messages ─────────────────────────────────────────────

    fun clearSuccessMessage() =
        _uiState.update { it.copy(successMessage = null) }

    fun clearErrorMessage() =
        _uiState.update { it.copy(errorMessage = null) }
}