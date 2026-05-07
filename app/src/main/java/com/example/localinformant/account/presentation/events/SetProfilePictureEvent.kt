package com.example.localinformant.account.presentation.events

import com.example.localinformant.core.domain.error.NetworkError

sealed interface SetProfilePictureEvent {
    data class ShowError(val message: NetworkError): SetProfilePictureEvent
}