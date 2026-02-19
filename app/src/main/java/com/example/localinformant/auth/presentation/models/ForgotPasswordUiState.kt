package com.example.localinformant.auth.presentation.models

import com.example.localinformant.auth.domain.error.ValidationError

data class ForgotPasswordUiState(
    val email: String? = null,
    val emailError: ValidationError? = ValidationError.EMPTY_FIELD,
    val isLoading: Boolean = false,
    val isSendEmailEnabled: Boolean = false
)
