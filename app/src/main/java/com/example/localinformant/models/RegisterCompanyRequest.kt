package com.example.localinformant.models

data class RegisterCompanyRequest(
    val companyName: String,
    val companyEmail: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)
