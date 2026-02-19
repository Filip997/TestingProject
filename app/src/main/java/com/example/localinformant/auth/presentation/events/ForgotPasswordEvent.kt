package com.example.localinformant.auth.presentation.events

import com.example.localinformant.auth.domain.error.AuthError

sealed interface ForgotPasswordEvent {
    object ShowSuccessDialog : ForgotPasswordEvent
    data class ShowError(val message: AuthError) : ForgotPasswordEvent
}