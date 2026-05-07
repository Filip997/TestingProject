package com.example.localinformant.core.domain.usecases

import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.repositories.GlobalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveUsersUseCase @Inject constructor(
    private val globalRepository: GlobalRepository
) {

    suspend operator fun invoke(userTypeIds: Map<String, UserType>): Flow<List<User>> {
        return globalRepository.observeUsersByIds(userTypeIds)
    }
}