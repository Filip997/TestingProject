package com.example.localinformant.core.domain.models


data class Post(
    val id: String = "",
    val createdAt: Long = 0,
    val userId: String = "",
    val postText: String = "",
    val imageUrls: List<String> = listOf(),
    val likeBtnClicked: Boolean = false,
    val likes: List<Reaction> = listOf(),
    val comments: List<Comment> = listOf()
)