package com.example.localinformant.conversations.domain.repositories

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Conversation
import com.example.localinformant.core.domain.models.Message
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.models.UserStatus
import com.example.localinformant.core.domain.result.Result
import kotlinx.coroutines.flow.Flow

interface ConversationsRepository {

    suspend fun getUserConversations(): Result<List<Conversation>, NetworkError>

    suspend fun getUserById(userId: String): Result<User, NetworkError>

    suspend fun searchPersonsByName(searchQuery: String): Result<List<Person>, NetworkError>
    suspend fun searchCompaniesByName(searchQuery: String): Result<List<Company>, NetworkError>

    suspend fun observeUserStatus(userId: String): Flow<UserStatus>

    suspend fun loadMessagesByConversationId(
        conversationId: String,
        initial: Boolean
    ): Result<List<Message>, NetworkError>
    suspend fun observeMessagesByConversationId(conversationId: String): Flow<List<Message>>
    suspend fun sendMessage(
        conversationId: String,
        receiverId: String,
        messageText: String
    ): Result<Unit, NetworkError>
}