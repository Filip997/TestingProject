package com.example.localinformant.core.data.dto

import com.google.firebase.Timestamp

data class ConversationDto(
    val id: String = "",
    val participants: List<String> = emptyList(),
    val messages: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageUserId: String = "",
    val lastMessageTime: Timestamp? = null
)
