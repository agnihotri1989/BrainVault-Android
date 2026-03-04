package com.kshitiz.brainvault.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

// Global event bus for auth events — using SharedFlow so it doesn't replay old events
object AuthEventBus {
    private val _events = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<AuthEvent> = _events

    sealed class AuthEvent {
        object TokenExpired : AuthEvent()
        object LoggedOut    : AuthEvent()
    }

    fun emit(event: AuthEvent) {
        _events.tryEmit(event)
    }
}