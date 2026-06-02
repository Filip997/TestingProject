package com.example.localinformant.conversations.presentation.models

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.presentation.models.ConversationUi

data class ConversationsUiState(
    val isLoading: Boolean = false,
    val conversations: List<ConversationUi> = emptyList(),
    val error: NetworkError? = null
)
