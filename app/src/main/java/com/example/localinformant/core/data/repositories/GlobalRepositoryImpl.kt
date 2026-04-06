package com.example.localinformant.core.data.repositories

import android.util.Log
import com.example.localinformant.core.data.api.FcmApi
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.core.data.dto.CompanyDto
import com.example.localinformant.core.data.dto.NotificationDto
import com.example.localinformant.core.data.dto.NotificationRequestDto
import com.example.localinformant.core.data.dto.PersonDto
import com.example.localinformant.core.data.mappers.toDomain
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.repositories.GlobalRepository
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.NotificationType
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.result.Result
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GlobalRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val fcmApi: FcmApi
) : GlobalRepository {

    override suspend fun updatePersonToken(token: String): Person? {
        return try {
            val currentUserId = auth.currentUser?.uid!!
            db.collection(AppConstants.PERSONS)
                .document(currentUserId)
                .update("token", token)
                .await()

            getPersonById(currentUserId)
        } catch (e: Exception) {
            Log.d("GlobalRepository", "Update token for person failed: ${e.toString()}")

            null
        }
    }

    override suspend fun updateCompanyToken(token: String): Company? {
        return try {
            val currentUserId = auth.currentUser?.uid!!
            db.collection(AppConstants.COMPANIES)
                .document(currentUserId)
                .update("token", token)
                .await()

            getCompanyById(currentUserId)
        } catch (e: Exception) {
            Log.d("GlobalRepository", "Update token for company failed: ${e.toString()}")

            null
        }
    }

    override suspend fun getPersonById(id: String): Person? {
        return db.collection(AppConstants.PERSONS)
            .document(id)
            .get()
            .await()
            .toObject(PersonDto::class.java)?.toDomain()
    }

    override suspend fun getCompanyById(id: String): Company? {
        return db.collection(AppConstants.COMPANIES)
                .document(id)
                .get()
                .await()
                .toObject(CompanyDto::class.java)?.toDomain()
    }

    override suspend fun saveNotificationToDatabase(
        id: String,
        fromUserId: String,
        fromUserType: UserType,
        notificationType: NotificationType,
        postId: String
    ): Result<Unit, NetworkError> {
        return try {
            val currentUserId = auth.currentUser?.uid!!

            val notificationDto = NotificationDto(
                id = id,
                createdOn = Timestamp.now(),
                fromUserId = fromUserId,
                fromUserType = fromUserType.name,
                toUserId = currentUserId,
                notificationType = notificationType.name,
                postId = postId
            )

            db.collection(AppConstants.NOTIFICATIONS)
                .add(notificationDto)
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    override suspend fun sendNotification(
        toUserIds: List<String>,
        fromUserType: UserType,
        notificationType: NotificationType,
        postId: String,
        message: String
    ): Result<Unit, NetworkError> {
        return try {
            val currentUserId = auth.currentUser?.uid!!

            val request = NotificationRequestDto(
                toUserIds = toUserIds,
                fromUserId = currentUserId,
                fromUserType = fromUserType.name,
                notificationType = notificationType.name,
                postId = postId,
                message = message
            )

            val result = fcmApi.sendNotification(request)

            if (result.isSuccessful) {
                Log.d("SendNotification", "Successful")
                Result.Success(Unit)
            } else {
                Log.d("SendNotification", "Failed")
                Result.Error(NetworkError.UNKNOWN)
            }
        } catch (e: Exception) {
            Log.d("SendNotification", e.message.toString())
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}