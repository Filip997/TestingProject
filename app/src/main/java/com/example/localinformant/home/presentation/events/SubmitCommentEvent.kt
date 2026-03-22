package com.example.localinformant.home.presentation.events

import com.example.localinformant.core.domain.error.NetworkError

sealed interface SubmitCommentEvent {
    data class ClearCommentText(val postId: String): SubmitCommentEvent
    data class ShowError(val message: NetworkError): SubmitCommentEvent
}