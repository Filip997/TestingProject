package com.example.localinformant.core.domain.models

data class UserAccountDetails(
    val user: User? = null,
    val userType: UserType? = null,
    val isCurrentUser: Boolean = false,
    val isUserFollowed: Boolean = false,
    val posts: List<PostWithCompany> = listOf()
)
