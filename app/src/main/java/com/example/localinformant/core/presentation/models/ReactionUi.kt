package com.example.localinformant.core.presentation.models

import com.example.localinformant.core.domain.models.UserType

data class ReactionUi(
    val id: String = "",
    val userId: String = "",
    val userType: UserType? = null,
    val userProfileImage: String = "",
    val userName: String = ""
)