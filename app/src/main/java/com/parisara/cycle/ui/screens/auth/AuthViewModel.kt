package com.parisara.cycle.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class AuthState {
    object Idle    : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String)      : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val currentUser: FirebaseUser? get() = auth.currentUser

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            runCatching {
                auth.signInWithEmailAndPassword(email, password).await()
            }.onSuccess { result ->
                result.user?.let {
                    _authState.value = AuthState.Success(it)
                } ?: run {
                    _authState.value = AuthState.Error("Sign in failed")
                }
            }.onFailure {
                _authState.value = AuthState.Error(
                    it.message ?: "Unknown error"
                )
            }
        }
    }

    fun createAccountWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            runCatching {
                auth.createUserWithEmailAndPassword(email, password).await()
            }.onSuccess { result ->
                result.user?.let {
                    _authState.value = AuthState.Success(it)
                } ?: run {
                    _authState.value = AuthState.Error("Account creation failed")
                }
            }.onFailure {
                _authState.value = AuthState.Error(
                    it.message ?: "Unknown error"
                )
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }
}