package com.example.localinformant.core.domain.usecases

import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.repositories.GlobalRepository
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val globalRepository: GlobalRepository
) {

    suspend operator fun invoke(userId: String, userType: UserType): User? {
        return when(userType) {
            UserType.PERSON -> globalRepository.getPersonById(userId)
            UserType.COMPANY -> globalRepository.getCompanyById(userId)
        }
    }
}