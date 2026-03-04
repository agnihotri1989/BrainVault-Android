package com.kshitiz.brainvault.network

import com.kshitiz.brainvault.BuildConfig
import com.kshitiz.brainvault.auth.AuthInterceptor
import com.kshitiz.brainvault.auth.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// Data classes for API
data class RegisterRequest(val email: String, val password: String)
data class RegisterResponse(val id: Int, val email: String, val created_at: String)
data class LoginResponse(val access_token: String, val token_type: String)
data class NoteRequest(val title: String, val content: String)
data class SyncResponse(val message: String, val id: String)
data class AskRequest(val message: String)
data class AskResponse(val reply: String, val source_notes: List<String>)

interface ApiService {

    // --- Auth (PUBLIC) ---
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") email: String,   // FastAPI OAuth2 expects "username" field
        @Field("password") password: String
    ): Response<LoginResponse>

    @POST("api/notes")
    suspend fun syncNote(@Body note: NoteRequest): Response<SyncResponse>

    @POST("api/chat")
    suspend fun askQuestion(@Body request: AskRequest): Response<AskResponse>
}

object RetrofitInstance {
    private const val BASE_URL = BuildConfig.BASE_URL


    private lateinit var tokenManager: TokenManager
    // 👇 Call this once from Application class before any API call
    fun init(tokenManager: TokenManager) {
        this.tokenManager = tokenManager
    }

    private val authInterceptor by lazy {
        AuthInterceptor(tokenManager)
    }

    private val logging by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)   // 👈 1st — attaches token
        .addInterceptor(logging)           // 👈 2nd — logs request WITH token
        .connectTimeout(30, TimeUnit.SECONDS)   // time to establish connection
        .readTimeout(120, TimeUnit.SECONDS)      // time to wait for model response
        .writeTimeout(30, TimeUnit.SECONDS)     // time to send request body
        .build()
    val api: ApiService by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}