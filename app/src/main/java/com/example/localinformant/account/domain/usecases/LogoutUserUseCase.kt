package com.example.localinformant.account.domain.usecases

import com.example.localinformant.account.domain.repositories.UserAccountRepository
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.result.Result
import javax.inject.Inject

class LogoutUserUseCase @Inject constructor(
    private val userAccountRepository: UserAccountRepository,
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(): Result<Unit, NetworkError> {
        val result = userAccountRepository.logout()

        return if (result is Result.Success) {
            preferencesRepository.deletePerson()
            preferencesRepository.deleteCompany()
            preferencesRepository.deleteUserType()

            result
        } else {
            result
        }
    }
}