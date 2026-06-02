package com.example.localinformant.conversations.domain.usecases

import com.example.localinformant.conversations.domain.repositories.ConversationsRepository
import javax.inject.Inject

class LoadMessagesUseCase @Inject constructor(
    private val conversationsRepository: ConversationsRepository
) {

     suspend operator fun invoke(conversationId: String, initial: Boolean = true) =
         conversationsRepository.loadMessagesByConversationId(conversationId, initial)
}