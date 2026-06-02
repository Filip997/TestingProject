package com.example.localinformant.conversations.domain.usecases

import com.example.localinformant.conversations.domain.repositories.ConversationsRepository
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.NotificationType
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.repositories.GlobalRepository
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.result.Result
import javax.inject.Inject

class SendMessageToConversationUseCase @Inject constructor(
    private val conversationsRepository: ConversationsRepository,
    private val globalRepository: GlobalRepository,
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(
        conversationId: String,
        receiverId: String,
        messageText: String
    ): Result<Unit, NetworkError> {
        return when (
            val result = conversationsRepository.sendMessage(conversationId, receiverId, messageText)
        ) {
            is Result.Success -> {
                val currentUserType = preferencesRepository.getUserType()?.let { UserType.valueOf(it) }

                globalRepository.sendNotification(
                    toUserIds = listOf(receiverId),
                    fromUserType = currentUserType ?: return result,
                    notificationType = NotificationType.NEW_MESSAGE,
                    postId = "",
                    message = messageText
                )

                result
            }

            is Result.Error -> {
                result
            }
        }
    }
}