package com.example.localinformant.models

import com.example.localinformant.core.domain.models.User

data class Person(
    var id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    val email: String = "",
    val token: String = "",
    val following: MutableList<String> = mutableListOf()
): User
