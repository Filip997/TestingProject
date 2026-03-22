package com.example.localinformant.home.domain.usecases

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.home.domain.repositories.HomeRepository
import java.util.UUID
import javax.inject.Inject

class SubmitReactionUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val homeRepository: HomeRepository
) {

    suspend operator fun invoke(postId: String): Result<Unit, NetworkError> {
        val userType = UserType.valueOf(preferencesRepository.getUserType()!!)
        val reactionId = UUID.randomUUID().toString()

        val person = preferencesRepository.getPerson()
        val company = preferencesRepository.getCompany()

        val user: User = when(userType) {
            UserType.PERSON -> person!!
            UserType.COMPANY -> company!!
        }

        return homeRepository.submitReaction(reactionId, postId, user, userType)
    }
}