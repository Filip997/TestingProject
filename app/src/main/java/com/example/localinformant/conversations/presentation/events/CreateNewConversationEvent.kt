package com.example.localinformant.conversations.presentation.events

import com.example.localinformant.core.domain.error.NetworkError

sealed interface CreateNewConversationEvent {
    data class Success(val userId: String, val conversationId: String): CreateNewConversationEvent
    data class ShowError(val message: NetworkError): CreateNewConversationEvent
}