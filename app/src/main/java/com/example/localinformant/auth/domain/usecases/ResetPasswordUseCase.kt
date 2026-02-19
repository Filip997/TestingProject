package com.example.localinformant.auth.domain.usecases

import com.example.localinformant.auth.domain.repositories.FirebaseAuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: FirebaseAuthRepository
) {

    suspend operator fun invoke(email: String) = authRepository.resetPassword(email)
}