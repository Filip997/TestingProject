package com.example.localinformant.core.domain.repositories

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Comment
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Conversation
import com.example.localinformant.core.domain.models.NotificationType
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.Post
import com.example.localinformant.core.domain.models.Reaction
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.result.Result
import kotlinx.coroutines.flow.Flow

interface GlobalRepository {

    suspend fun updatePersonToken(token: String): Person?
    suspend fun updateCompanyToken(token: String): Company?

    suspend fun getPersonById(id: String): Person?
    suspend fun getCompanyById(id: String): Company?

    suspend fun getUserTypeByUserId(userId: String): Result<UserType, NetworkError>

    suspend fun observeUsersByIds(userTypeIds: Map<String, UserType>): Flow<List<User>>

    suspend fun submitReaction(id: String, postId: String, user: User, userType: UserType): Result<Post, NetworkError>
    fun observeReactions(postIds: List<String>): Flow<List<Reaction>>

    suspend fun submitComment(id: String, postId: String, commentText: String, user: User, userType: UserType): Result<Post, NetworkError>
    fun observeComments(postIds: List<String>): Flow<List<Comment>>

    suspend fun getUsersWhoCommentedByPostId(postId: String, postUserId: String): Result<List<String>, NetworkError>

    suspend fun checkIfConversationExists(otherUserId: String, otherUserType: UserType): Result<Conversation?, NetworkError>
    suspend fun createNewConversation(otherUserId: String, otherUserType: UserType): Result<String, NetworkError>

    suspend fun saveNotificationToDatabase(
        id: String,
        fromUserId: String,
        fromUserType: UserType,
        notificationType: NotificationType,
        postId: String
    ): Result<Unit, NetworkError>

    suspend fun sendNotification(
        toUserIds: List<String>,
        fromUserType: UserType,
        notificationType: NotificationType,
        postId: String,
        message: String
    ): Result<Unit, NetworkError>
}