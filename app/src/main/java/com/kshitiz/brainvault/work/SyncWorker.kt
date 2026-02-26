package com.kshitiz.brainvault.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kshitiz.brainvault.data.BrainVaultDatabase
import com.kshitiz.brainvault.network.NoteRepository

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val dao = BrainVaultDatabase.getDatabase(applicationContext).noteDao()
            val repository = NoteRepository(dao)
            repository.syncUnsyncedNotes()
            Result.success()
        } catch (e: Exception) {
            Result.retry() // auto retries on failure
        }
    }
}