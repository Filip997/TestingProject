package com.example.localinformant.core.domain.models

data class Comment(
    val id: String = "",
    val createdAt: Long = 0,
    val userId: String = "",
    val userType: UserType? = null,
    val userProfileImage: String = "",
    val userName: String = "",
    val postId: String = "",
    val commentText: String = ""
)