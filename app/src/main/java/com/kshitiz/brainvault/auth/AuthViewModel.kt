package com.kshitiz.brainvault.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle    : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String)   : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application)
    private val repository   = AuthRepository(tokenManager)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun register(email: String, password: String) = viewModelScope.launch {
        _authState.value = AuthState.Loading
        val result = repository.register(email, password)
        _authState.value = result.fold(
            onSuccess = { AuthState.Success("Account created! Please login.") },
            onFailure = { AuthState.Error(it.message ?: "Unknown error") }
        )
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _authState.value = AuthState.Loading
        val result = repository.login(email, password)
        _authState.value = result.fold(
            onSuccess = { AuthState.Success("Login successful") },
            onFailure = { AuthState.Error(it.message ?: "Unknown error") }
        )
    }

    fun logout() {
        repository.logout()
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}