package com.example.localinformant.core.presentation.models

data class PostUiState(
    val postsUi: List<PostUi> = listOf(),
    val currentUserProfileImage: String = ""
)
