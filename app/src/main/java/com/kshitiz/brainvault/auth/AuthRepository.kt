package com.kshitiz.brainvault.auth

import com.kshitiz.brainvault.network.RegisterRequest
import com.kshitiz.brainvault.network.RegisterResponse
import com.kshitiz.brainvault.network.RetrofitInstance

class AuthRepository(private val tokenManager: TokenManager) {

    suspend fun register(email: String, password: String): Result<RegisterResponse> {
        return try {
            val response = RetrofitInstance.api.register(RegisterRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = when (response.code()) {
                    400 -> "Email already registered"
                    else -> "Registration failed"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error — check your connection"))
        }
    }

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val response = RetrofitInstance.api.login(email, password)
            if (response.isSuccessful && response.body() != null) {
                val token = response.body()!!.access_token
                tokenManager.saveToken(token)         // 💾 persist token
                Result.success(token)
            } else {
                Result.failure(Exception("Incorrect email or password"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error — check your connection"))
        }
    }

    fun logout() {
        tokenManager.clearToken()
        AuthEventBus.emit(AuthEventBus.AuthEvent.LoggedOut)
    }


    // 👇 Call this before any protected API call
//    fun checkTokenValidity() {
//        if (tokenManager.isTokenExpired()) {
//            tokenManager.clearToken()
//            AuthEventBus.emit(AuthEventBus.AuthEvent.TokenExpired)  // 👈 broadcast expiry
//        }
//    }
}