package com.example.localinformant.core.presentation.models

import com.example.localinformant.core.domain.models.UserType

data class CommentUi(
    val id: String = "",
    val createdAt: Long = 0,
    val userId: String = "",
    val userType: UserType? = null,
    val userProfileImage: String = "",
    val userName: String = "",
    val commentText: String = ""
)
