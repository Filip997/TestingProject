package com.example.localinformant.conversations.presentation.events

import com.example.localinformant.core.domain.error.NetworkError

sealed interface SendMessageEvent {
    object Success: SendMessageEvent
    data class ShowError(val message: NetworkError): SendMessageEvent
}