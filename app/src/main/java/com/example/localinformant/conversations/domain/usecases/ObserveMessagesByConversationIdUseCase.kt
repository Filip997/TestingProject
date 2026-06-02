package com.example.localinformant.conversations.domain.usecases

import com.example.localinformant.conversations.domain.repositories.ConversationsRepository
import javax.inject.Inject

class ObserveMessagesByConversationIdUseCase @Inject constructor(
    private val conversationsRepository: ConversationsRepository
) {

     suspend operator fun invoke(conversationId: String) =
         conversationsRepository.observeMessagesByConversationId(conversationId)
}