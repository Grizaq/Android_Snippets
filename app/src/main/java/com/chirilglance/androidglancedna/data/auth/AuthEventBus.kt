package com.chirilglance.androidglancedna.data.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Event bus for authentication-related events.
 *
 * This class provides a way for different parts of the application to communicate
 * about authentication events without direct coupling.
 */
@Singleton
class AuthEventBus @Inject constructor() {

    /**
     * Represents different types of authentication events
     */
    sealed class AuthEvent {
        /**
         * Authentication failed (e.g., token expired, invalid credentials)
         */
        data object AuthFailure : AuthEvent()

        /**
         * User successfully logged in
         * @property token The new access token
         */
        data class Login(val token: String) : AuthEvent()

        /**
         * User logged out
         */
        data object Logout : AuthEvent()

        /**
         * Token was refreshed
         * @property newToken The new access token
         */
        data class TokenRefresh(val newToken: String) : AuthEvent()
    }

    private val _authEvents = MutableSharedFlow<AuthEvent>()

    /**
     * Observable flow of authentication events
     */
    val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()

    /**
     * Emits an authentication event
     * @param event The event to emit
     */
    suspend fun emitAuthEvent(event: AuthEvent) {
        _authEvents.emit(event)
    }
}