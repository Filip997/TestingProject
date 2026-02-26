package com.example.localinformant.main.presentation.events

import com.example.localinformant.core.domain.error.NetworkError

sealed interface CreatePostEvent {
    object ClosePopUpWindow: CreatePostEvent
    class ShowError(val error: NetworkError): CreatePostEvent
}