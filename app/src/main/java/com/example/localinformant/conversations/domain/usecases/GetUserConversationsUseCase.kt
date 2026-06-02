package com.example.localinformant.conversations.domain.usecases

import com.example.localinformant.conversations.domain.repositories.ConversationsRepository
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Conversation
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.result.Result
import javax.inject.Inject

class GetUserConversationsUseCase @Inject constructor(
    private val conversationsRepository: ConversationsRepository
) {

    suspend operator fun invoke(): Result<List<Conversation>, NetworkError> {
        return when (val result = conversationsRepository.getUserConversations()) {
            is Result.Success -> {
                val conversations = result.data

                val updatedConversations = conversations.map {
                    when (val userResult = conversationsRepository.getUserById(it.participant2Id)) {
                        is Result.Success -> {
                            val user = userResult.data

                            it.copy(
                                participant2Name = when(user) {
                                    is Person -> user.fullName
                                    is Company -> user.companyName
                                    else -> ""
                                },
                                participant2ProfileImage = when(user) {
                                    is Person -> user.profileImageUrl
                                    is Company -> user.companyProfileImageUrl
                                    else -> ""
                                }
                            )
                        }
                        is Result.Error -> {
                            it
                        }
                    }
                }.filter { it.messages.isNotEmpty() }

                Result.Success(updatedConversations)
            }
            is Result.Error -> result
        }
    }
}