package com.example.localinformant.core.domain.usecases

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.repositories.GlobalRepository
import com.example.localinformant.core.domain.result.Result
import javax.inject.Inject

class StartConversationUseCase @Inject constructor(
    private val globalRepository: GlobalRepository
) {

    suspend operator fun invoke(userId: String, userType: UserType): Result<String, NetworkError> {
        return when(val conversationResult = globalRepository.checkIfConversationExists(
            userId, userType
        )) {
            is Result.Success -> {
                val conversation = conversationResult.data

                if (conversation == null) {
                    when(val newConvoResult = globalRepository.createNewConversation(userId, userType)) {
                        is Result.Success -> {
                            val newConvoId = newConvoResult.data
                            Result.Success(newConvoId)
                        }
                        is Result.Error -> {
                            newConvoResult
                        }
                    }
                } else {
                    Result.Success(conversation.id)
                }
            }
            is Result.Error -> {
                conversationResult
            }
        }
    }
}