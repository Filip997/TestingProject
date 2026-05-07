package com.example.localinformant.core.presentation.models

import com.example.localinformant.core.domain.models.UserType

data class UserAccountDetailsUi(
    val userId: String = "",
    val userType: UserType? = null,
    val isCurrentUser: Boolean = false,
    val isUserFollowed: Boolean = false,
    val userProfileImage: String = "",
    val userName: String = "",
    val followers: List<String> = listOf(),
    val following: List<String> = listOf(),
    val postsUi: List<PostUi> = listOf()
)
