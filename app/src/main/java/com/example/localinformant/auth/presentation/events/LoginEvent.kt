package com.example.localinformant.auth.presentation.events

import com.example.localinformant.auth.domain.error.AuthError

sealed interface LoginEvent {
    object NavigateToHome : LoginEvent
    data class ShowError(val message: AuthError) : LoginEvent
}