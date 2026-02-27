package com.kshitiz.brainvault.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// Data classes for API
data class NoteRequest(val title: String, val content: String)
data class SyncResponse(val message: String, val id: String)
data class AskRequest(val message: String)
data class AskResponse(val reply: String, val source_notes: List<String>)

interface ApiService {

    @POST("api/notes")
    suspend fun syncNote(@Body note: NoteRequest): Response<SyncResponse>

    @POST("api/chat")
    suspend fun askQuestion(@Body request: AskRequest): Response<AskResponse>
}

object RetrofitInstance {
    //private const val BASE_URL = "http://10.0.2.2:8000/"  // use 10.0.2.2 for emulator
    //private const val BASE_URL = "http://13.60.211.20:8000/" // AWS URL
    private const val BASE_URL = "http://13.60.211.20:8000/"
    val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
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