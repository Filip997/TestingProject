package com.example.localinformant.account.domain.usecases

import com.example.localinformant.account.domain.repositories.UserAccountRepository
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.result.Result
import javax.inject.Inject

class GetUsersByIdsUseCase @Inject constructor(
    private val userAccountRepository: UserAccountRepository
) {

    suspend operator fun invoke(userIds: List<String>): Result<List<User>, NetworkError> {
        return userAccountRepository.getUsersByIds(userIds)
    }
}