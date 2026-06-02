package com.example.localinformant.conversations.domain.usecases

import com.example.localinformant.conversations.domain.repositories.ConversationsRepository
import javax.inject.Inject

class ObserveUserStatusUseCase @Inject constructor(
    private val conversationsRepository: ConversationsRepository
) {

    suspend operator fun invoke(userId: String) = conversationsRepository.observeUserStatus(userId)
}