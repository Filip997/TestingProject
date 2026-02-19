package com.example.localinformant.auth.presentation.models

import com.example.localinformant.auth.domain.error.ValidationError
import com.example.localinformant.core.domain.models.UserType

data class LoginUiState(
    val userType: UserType? = null,
    val email: String? = null,
    val emailError: ValidationError? = ValidationError.EMPTY_FIELD,
    val password: String? = null,
    val passwordError: ValidationError? = ValidationError.EMPTY_FIELD,
    val isLoginEnabled: Boolean = false,
    val isLoading: Boolean = false
)
