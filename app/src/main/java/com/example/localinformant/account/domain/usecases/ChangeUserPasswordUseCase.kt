package com.example.localinformant.account.domain.usecases

import com.example.localinformant.account.domain.repositories.UserAccountRepository
import javax.inject.Inject

class ChangeUserPasswordUseCase @Inject constructor(
    private val userAccountRepository: UserAccountRepository
) {

    suspend operator fun invoke(oldPassword: String, newPassword: String) =
        userAccountRepository.changePassword(oldPassword, newPassword)
}