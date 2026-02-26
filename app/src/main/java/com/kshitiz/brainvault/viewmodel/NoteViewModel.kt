package com.kshitiz.brainvault.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kshitiz.brainvault.data.BrainVaultDatabase
import com.kshitiz.brainvault.data.Note
import com.kshitiz.brainvault.network.NoteRepository
import com.kshitiz.brainvault.work.SyncWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    val allNotes: StateFlow<List<Note>>

    // AI Answer state
    private val _aiAnswer = MutableStateFlow("")
    val aiAnswer: StateFlow<String> = _aiAnswer

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        val dao = BrainVaultDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(dao)
        allNotes = repository.allNotes.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        scheduleSyncWorker()
    }

    fun addNote(title: String, content: String) = viewModelScope.launch {
        val note = Note(title = title, content = content, timestamp = System.currentTimeMillis())
        repository.insertNote(note)
        repository.syncUnsyncedNotes() // attempt sync immediately
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        repository.deleteNote(note)
    }

    fun askAI(question: String) = viewModelScope.launch {
        _isLoading.value = true
        _aiAnswer.value = repository.askAI(question)
        _isLoading.value = false
    }

    private fun scheduleSyncWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // only runs when internet is available
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(getApplication()).enqueueUniquePeriodicWork(
            "brainvault_sync",
            ExistingPeriodicWorkPolicy.KEEP, // won't duplicate if already scheduled
            syncRequest
        )
    }
    fun clearAiAnswer() {
        _aiAnswer.value = ""
    }
}