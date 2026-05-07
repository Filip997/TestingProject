package com.example.localinformant.account.presentation.events

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.presentation.models.FollowerFollowingUserUi

sealed interface OpenFollowersFollowingPopUpWindowEvent {
    data class Success(val usersUi: List<FollowerFollowingUserUi>): OpenFollowersFollowingPopUpWindowEvent
    data class ShowError(val message: NetworkError): OpenFollowersFollowingPopUpWindowEvent
}