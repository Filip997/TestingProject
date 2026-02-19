package com.example.localinformant.auth.domain.usecases

sealed class RegisterUserData {
    data class Person(
        val firstName: String,
        val lastName: String,
        val email: String,
        val password: String
    ): RegisterUserData()

    data class Company(
        val companyName: String,
        val companyEmail: String,
        val firstName: String,
        val lastName: String,
        val email: String,
        val password: String
    ): RegisterUserData()
}