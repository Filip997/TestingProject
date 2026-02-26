package com.example.localinformant.main.presentation.models

import com.example.localinformant.core.domain.models.UserType

data class MainUiState(
    val userType: UserType? = null,
    val isLoading: Boolean = false
)
