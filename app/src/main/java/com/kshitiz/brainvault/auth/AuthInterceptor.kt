package com.kshitiz.brainvault.auth

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()

        // 👇 Skip token attachment for public endpoints — they don't need it
        val isPublicEndpoint = originalRequest.url.encodedPath.contains("/auth/")
        if (isPublicEndpoint) {
            return chain.proceed(originalRequest)
        }

        // 👇 Attach token to every protected request
        val token = tokenManager.getToken()
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        val response = chain.proceed(authenticatedRequest)

        // 👇 Catch 401 at network level — backend says token is invalid/expired
        if (response.code == 401) {
            tokenManager.clearToken()
            AuthEventBus.emit(AuthEventBus.AuthEvent.TokenExpired)
        }

        return response
    }
}