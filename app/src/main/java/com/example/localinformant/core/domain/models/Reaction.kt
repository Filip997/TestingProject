package com.example.localinformant.core.domain.models

data class Reaction(
    val id: String = "",
    val userId: String = "",
    val userType: UserType? = null,
    val userProfileImage: String = "",
    val userName: String = "",
    val postId: String = "",
)
