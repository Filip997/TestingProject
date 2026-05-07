package com.example.localinformant.core.presentation.models

import com.example.localinformant.core.domain.models.UserType

data class FollowerFollowingUserUi(
    val userId: String = "",
    val userType: UserType? = null,
    val userProfileImage: String = "",
    val userName: String = ""
)
