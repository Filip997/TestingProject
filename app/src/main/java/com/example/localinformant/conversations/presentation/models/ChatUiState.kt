package com.example.localinformant.conversations.presentation.models

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.UserStatus
import com.example.localinformant.core.presentation.models.MessageUi

data class ChatUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isLoadingGoingToUserProfile: Boolean = false,
    val messages: List<MessageUi> = emptyList(),
    val participant2Id: String = "",
    val participant2Name: String = "",
    val participant2ProfileImage: String = "",
    val participant2Status: UserStatus = UserStatus.OFFLINE,
    val error: NetworkError? = null
)
