package com.example.localinformant.core.domain.models

import com.example.localinformant.models.Comment

data class Post(
    val id: String = "",
    val companyId: String = "",
    val companyName: String = "",
    val postText: String = "",
    val likes: List<String> = listOf(),
    val comments: List<Comment> = listOf()
)