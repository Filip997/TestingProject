package com.example.localinformant.account.presentation.models

import com.example.localinformant.core.domain.error.ValidationError

data class SettingsUiState(
    val oldPassword: String? = null,
    val oldPasswordError: ValidationError? = ValidationError.EMPTY_FIELD,
    val newPassword: String? = null,
    val newPasswordError: ValidationError? = ValidationError.EMPTY_FIELD,
    val isChangePasswordEnabled: Boolean = false,
    val isLoading: Boolean = false
)
