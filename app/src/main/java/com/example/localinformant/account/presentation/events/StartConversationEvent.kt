package com.example.localinformant.account.presentation.events

import com.example.localinformant.core.domain.error.NetworkError

sealed interface StartConversationEvent {
    data class Success(val conversationId: String): StartConversationEvent
    data class ShowError(val message: NetworkError): StartConversationEvent
}