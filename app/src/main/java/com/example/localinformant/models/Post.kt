package com.example.localinformant.models

data class Post(
    val id: String = "",
    val companyId: String = "",
    val companyName: String = "",
    val postText: String = "",
    val likes: List<String> = listOf(),
    val comments: List<Comment> = listOf()
)
