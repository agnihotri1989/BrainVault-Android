package com.kshitiz.brainvault.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

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
    private const val BASE_URL = "http://10.0.2.2:8000/"  // use 10.0.2.2 for emulator

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}