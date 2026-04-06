package com.example.localinformant.core.data.dto

import com.google.firebase.Timestamp

data class NotificationDto(
    val id: String = "",
    val createdOn: Timestamp? = null,
    val toUserId: String = "",
    val fromUserId: String = "",
    val fromUserType: String = "",
    val postId: String = "",
    val notificationType: String = ""
)
