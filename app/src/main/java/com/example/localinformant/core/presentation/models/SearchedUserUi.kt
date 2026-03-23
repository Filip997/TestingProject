package com.example.localinformant.core.presentation.models

import com.example.localinformant.core.domain.models.UserType

data class SearchedUserUi(
    val id: String = "",
    val userType: UserType? = null,
    val userName: String = "",
    val userProfileImageUrl: String = ""
)
