package com.example.localinformant.core.domain.models

data class Conversation(
    val id: String = "",
    val participants: List<String> = emptyList(),
    val participant2Id: String = "",
    val participant2Name: String = "",
    val participant2ProfileImage: String = "",
    val messages: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageUserId: String = "",
    val lastMessageTime: Long = 0L
)
