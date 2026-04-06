package com.example.localinformant.core.domain.repositories

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.NotificationType
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.result.Result

interface GlobalRepository {

    suspend fun updatePersonToken(token: String): Person?
    suspend fun updateCompanyToken(token: String): Company?

    suspend fun getPersonById(id: String): Person?
    suspend fun getCompanyById(id: String): Company?

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