package com.example.localinformant.account.domain.usecases

import com.example.localinformant.account.domain.repositories.UserAccountRepository
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.result.Result
import javax.inject.Inject

class FollowUnfollowCompanyUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val userAccountRepository: UserAccountRepository
) {

    suspend operator fun invoke(userId: String?, shouldFollow: Boolean): Result<User, NetworkError> {
        val currentUserType = preferencesRepository.getUserType()?.let { UserType.valueOf(it) }

        return if (currentUserType != null && userId != null) {
            val result = if (shouldFollow) {
                userAccountRepository.followCompany(currentUserType, userId)
            } else {
                userAccountRepository.unfollowCompany(currentUserType, userId)
            }

            when(result) {
                is Result.Success -> {
                    when(val user = result.data) {
                        is Person -> {
                            preferencesRepository.savePerson(user)
                            Result.Success(user)
                        }
                        is Company -> {
                            preferencesRepository.saveCompany(user)
                            Result.Success(user)
                        }
                        else -> Result.Error(NetworkError.UNKNOWN)
                    }
                }
                is Result.Error -> result
            }
        } else {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}