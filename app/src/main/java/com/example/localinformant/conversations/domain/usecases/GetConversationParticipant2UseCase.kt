package com.example.localinformant.conversations.domain.usecases

import com.example.localinformant.conversations.domain.repositories.ConversationsRepository
import javax.inject.Inject

class GetConversationParticipant2UseCase @Inject constructor(
    private val conversationsRepository: ConversationsRepository
) {

    suspend operator fun invoke(otherUserId: String) = conversationsRepository.getUserById(otherUserId)
}