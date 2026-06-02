package com.example.localinformant.main.domain.usecases

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.UserStatus
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.main.domain.repositories.MainRepository
import javax.inject.Inject

class UpdateCurrentUserStatusUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val mainRepository: MainRepository
) {

    suspend operator fun invoke(status: UserStatus): Result<Unit, NetworkError> {
        val userType = preferencesRepository.getUserType()?.let { UserType.valueOf(it) }

        return if (userType != null) {
            mainRepository.changeUserStatus(userType, status)
        } else {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}