package com.example.localinformant.core.data.dto

import com.example.localinformant.models.Comment

data class PostDto(
    val id: String = "",
    val userId: String = "",
    val postText: String = "",
    val imageUrls: List<String> = listOf(),
    val likes: List<String> = listOf(),
    val comments: List<Comment> = listOf()
)
