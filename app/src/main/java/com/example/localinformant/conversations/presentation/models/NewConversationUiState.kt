package com.example.localinformant.conversations.presentation.models

import com.example.localinformant.core.presentation.models.SearchedUserUi

data class NewConversationUiState(
    val isLoading: Boolean = false,
    val searchedUsers: List<SearchedUserUi> = listOf()
)
