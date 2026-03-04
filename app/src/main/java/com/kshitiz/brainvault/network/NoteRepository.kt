package com.kshitiz.brainvault.network

import android.util.Log
import com.kshitiz.brainvault.data.Note
import com.kshitiz.brainvault.data.NoteDao
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val dao: NoteDao) {

    val allNotes: Flow<List<Note>> = dao.getAllNotes()

    suspend fun insertNote(note: Note): Long = dao.insertNote(note)

    suspend fun deleteNote(note: Note) = dao.deleteNote(note)

    // Sync offline notes to FastAPI + Pinecone
    suspend fun syncUnsyncedNotes() {
        val unsynced = dao.getUnsyncedNotes()
        unsynced.forEach { note ->
            try {
                val response = RetrofitInstance.api.syncNote(
                    NoteRequest(note.title, note.content)
                )
                if (response.isSuccessful) {
                    dao.markAsSynced(note.id)
                }
            } catch (e: Exception) {
                Log.e("NoteRepository", "Error syncing note: ${e.message}")
                // Offline — will retry next time
            }
        }
    }

    // Ask AI question against your notes
    suspend fun askAI(question: String): String {
        return try {
            val response = RetrofitInstance.api.askQuestion(AskRequest(question))
            when {
                response.isSuccessful -> response.body()?.reply ?: "No answer found"
                response.code() == 401 -> "Session expired — please login again"  // fallback msg
                else -> "Server error: ${response.code()}"
            }
        } catch (e: Exception) {
            "Offline — AI unavailable"
        }
    }
}