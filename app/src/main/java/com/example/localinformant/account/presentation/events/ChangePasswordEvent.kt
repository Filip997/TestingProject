package com.example.localinformant.account.presentation.events

import com.example.localinformant.account.domain.error.ChangePassError

sealed interface ChangePasswordEvent {
    object Success: ChangePasswordEvent
    data class ShowError(val message: ChangePassError): ChangePasswordEvent
}