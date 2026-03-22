package com.example.localinformant.core.data.dto

import com.google.firebase.Timestamp

data class PostDto(
    val id: String = "",
    val createdAt: Timestamp? = null,
    val userId: String = "",
    val postText: String = "",
    val imageUrls: List<String> = listOf(),
    val likes: List<String> = listOf(),
    val comments: List<String> = listOf()
)
