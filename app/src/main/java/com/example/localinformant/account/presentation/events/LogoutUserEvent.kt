package com.example.localinformant.account.presentation.events

import com.example.localinformant.core.domain.error.NetworkError

sealed interface LogoutUserEvent {
    object Success: LogoutUserEvent
    data class ShowError(val message: NetworkError): LogoutUserEvent
}