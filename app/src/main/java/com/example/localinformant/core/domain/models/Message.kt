package com.example.localinformant.core.domain.models

data class Message(
    val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timeSent: Long = 0L
)