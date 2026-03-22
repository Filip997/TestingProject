package com.example.localinformant.core.data.dto

import com.google.firebase.Timestamp

data class CommentDto(
    val id: String = "",
    val createdAt: Timestamp? = null,
    val userId: String = "",
    val userType: String = "",
    val userProfileImage: String = "",
    val userName: String = "",
    val postId: String = "",
    val commentText: String = ""
)
