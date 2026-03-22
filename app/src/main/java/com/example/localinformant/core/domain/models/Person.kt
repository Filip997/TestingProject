package com.example.localinformant.core.domain.models

data class Person(
    val id: String = "",
    val profileImageUrl: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    val email: String = "",
    val token: String = "",
    val following: List<String> = listOf()
): User