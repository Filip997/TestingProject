package com.example.localinformant.models

data class LoginRegisterPersonResponse(
    val isSuccessful: Boolean,
    val message: String?,
    val person: Person?
)
