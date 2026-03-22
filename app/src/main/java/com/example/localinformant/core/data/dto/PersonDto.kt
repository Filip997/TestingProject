package com.example.localinformant.core.data.dto

data class PersonDto(
    var id: String = "",
    val profileImageUrl: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val token: String = "",
    val following: List<String> = listOf()
)
