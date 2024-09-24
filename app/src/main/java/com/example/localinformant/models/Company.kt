package com.example.localinformant.models

data class Company(
    val id: String = "",
    val companyName: String = "",
    val companyEmail: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val token: String = "",
    val followers: Int = 0
)
