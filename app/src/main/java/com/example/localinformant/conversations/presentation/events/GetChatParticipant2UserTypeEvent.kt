package com.example.localinformant.conversations.presentation.events

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.UserType

sealed interface GetChatParticipant2UserTypeEvent {
    data class Success(val userType: UserType): GetChatParticipant2UserTypeEvent
    data class ShowError(val message: NetworkError): GetChatParticipant2UserTypeEvent
}