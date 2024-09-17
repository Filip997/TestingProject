package com.example.localinformant.models

data class Person(
    var id: String? = null,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)
