package com.example.localinformant.models

data class Person(
    var id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    val email: String = "",
    val token: String = "",
    val following: List<String> = listOf()
)
