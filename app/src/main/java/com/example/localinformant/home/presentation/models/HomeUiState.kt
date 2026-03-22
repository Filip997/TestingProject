package com.example.localinformant.home.presentation.models

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.presentation.models.PostUiState

data class HomeUiState(
    val isLoading: Boolean = false,
    val postsUi: List<PostUiState> = listOf(),
    val error: NetworkError? = null
)
