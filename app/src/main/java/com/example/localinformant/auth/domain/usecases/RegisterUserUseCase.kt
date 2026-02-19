package com.example.localinformant.auth.domain.usecases

import com.example.localinformant.auth.domain.repositories.FirebaseAuthRepository
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.error.Error
import com.example.localinformant.core.domain.result.Result
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: FirebaseAuthRepository
) {

    suspend operator fun invoke(input: RegisterUserData): Result<User, Error> {
        return when(input) {
            is RegisterUserData.Company -> authRepository.registerCompany(input)
            is RegisterUserData.Person -> authRepository.registerPerson(input)
        }
    }
}