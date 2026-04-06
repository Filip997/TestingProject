package com.example.localinformant.core.presentation.models

import com.example.localinformant.core.domain.models.NotificationType
import com.example.localinformant.core.domain.models.UserType

data class NotificationUi(
    val id: String = "",
    val createdOn: Long = 0,
    val toUserId: String = "",
    val fromUserId: String = "",
    val fromUserType: UserType? = null,
    val fromUserName: String = "",
    val fromUserProfileImageUrl: String = "",
    val postId: String = "",
    val notificationType: NotificationType? = null
)
