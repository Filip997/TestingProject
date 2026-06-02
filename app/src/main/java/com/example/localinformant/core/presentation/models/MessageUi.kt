package com.example.localinformant.core.presentation.models

data class MessageUi(
    val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timeSent: Long = 0L
)
