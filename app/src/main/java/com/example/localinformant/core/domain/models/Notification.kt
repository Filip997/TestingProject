package com.example.localinformant.core.domain.models

data class Notification(
    val id: String = "",
    val createdOn: Long = 0,
    val toUserId: String = "",
    val fromUser: User? = null,
    val fromUserType: UserType? = null,
    val postId: String = "",
    val notificationType: NotificationType? = null
)