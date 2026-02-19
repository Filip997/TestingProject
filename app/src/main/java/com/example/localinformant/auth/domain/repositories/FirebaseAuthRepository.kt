package com.example.localinformant.auth.domain.repositories

import com.example.localinformant.auth.domain.usecases.RegisterUserData
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.error.Error
import com.example.localinformant.core.domain.result.Result

interface FirebaseAuthRepository {

    suspend fun loginPerson(email: String, password: String): Result<User, Error>
    suspend fun loginCompany(email: String, password: String): Result<User, Error>

    suspend fun registerPerson(request: RegisterUserData.Person): Result<User, Error>
    suspend fun registerCompany(request: RegisterUserData.Company): Result<User, Error>

    suspend fun resetPassword(email: String): Result<Unit, Error>
}