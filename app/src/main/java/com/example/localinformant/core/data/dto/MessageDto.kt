package com.example.localinformant.core.data.dto

import com.google.firebase.Timestamp

data class MessageDto(
    val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timeSent: Timestamp? = null
)
