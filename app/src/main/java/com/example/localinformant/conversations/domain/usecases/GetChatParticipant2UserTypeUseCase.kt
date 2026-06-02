package com.example.localinformant.conversations.domain.usecases

import com.example.localinformant.core.domain.repositories.GlobalRepository
import javax.inject.Inject

class GetChatParticipant2UserTypeUseCase @Inject constructor(
    private val globalRepository: GlobalRepository
) {

    suspend operator fun invoke(userId: String) = globalRepository.getUserTypeByUserId(userId)
}