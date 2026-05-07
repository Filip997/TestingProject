package com.example.localinformant.account.presentation.events

import com.example.localinformant.core.domain.error.NetworkError

sealed interface FollowUnfollowCompanyEvent {
    data class ShowError(val message: NetworkError): FollowUnfollowCompanyEvent
}