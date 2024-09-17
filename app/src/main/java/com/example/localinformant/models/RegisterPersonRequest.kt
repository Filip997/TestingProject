package com.example.localinformant.models

data class RegisterPersonRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)
