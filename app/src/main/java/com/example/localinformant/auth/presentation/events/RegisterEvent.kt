package com.example.localinformant.auth.presentation.events

import com.example.localinformant.auth.domain.error.AuthError

sealed interface RegisterEvent {
    object NavigateToLogin : RegisterEvent
    data class ShowError(val message: AuthError) : RegisterEvent
}