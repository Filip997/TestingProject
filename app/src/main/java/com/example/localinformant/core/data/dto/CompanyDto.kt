package com.example.localinformant.core.data.dto

data class CompanyDto(
    val id: String = "",
    val companyName: String = "",
    val companyProfileImageUrl: String = "",
    val companyEmail: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val token: String = "",
    val followers: List<String> = listOf(),
    val following: List<String> = listOf(),
    val posts: List<String> = listOf()
)
