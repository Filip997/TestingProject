package com.example.localinformant.models

data class LoginRegisterCompanyResponse(
    val isSuccessful: Boolean,
    val message: String?,
    val company: Company?
)
