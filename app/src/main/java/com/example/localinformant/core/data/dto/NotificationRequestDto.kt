package com.example.localinformant.core.data.dto

data class NotificationRequestDto(
    val toUserIds: List<String> = listOf(),
    val fromUserId: String = "",
    val fromUserType: String = "",
    val notificationType: String = "",
    val postId: String = "",
    val message: String = ""
)