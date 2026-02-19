package com.example.localinformant.models

import com.example.localinformant.core.domain.models.User

data class Company(
    val id: String = "",
    val companyName: String = "",
    val companyEmail: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val token: String = "",
    val followers: MutableList<String> = mutableListOf()
): User
