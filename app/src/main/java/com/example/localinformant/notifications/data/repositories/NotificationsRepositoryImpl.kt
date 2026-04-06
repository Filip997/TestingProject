package com.example.localinformant.notifications.data.repositories

import com.example.localinformant.constants.AppConstants
import com.example.localinformant.core.data.dto.CompanyDto
import com.example.localinformant.core.data.dto.NotificationDto
import com.example.localinformant.core.data.dto.PersonDto
import com.example.localinformant.core.data.mappers.toDomain
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Notification
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.network.NetworkChecker
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.notifications.domain.repositories.NotificationsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val networkChecker: NetworkChecker
) : NotificationsRepository {

    override suspend fun getUserNotifications(): Result<List<Notification>, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val currentUserId = auth.currentUser?.uid!!

            val notifications = db.collection(AppConstants.NOTIFICATIONS)
                .whereEqualTo("toUserId", currentUserId)
                .get()
                .await()
                .mapNotNull {
                    it.toObject(NotificationDto::class.java)
                }
                .map {
                    it.toDomain()
                }
                .map { notification ->
                    when(notification.fromUserType!!) {
                        UserType.PERSON -> {
                            val personDto = getPersonById((notification.fromUser as Person).id)

                            if (personDto != null) {
                                notification.copy(
                                    fromUser = personDto.toDomain()
                                )
                            } else {
                                notification
                            }
                        }
                        UserType.COMPANY -> {
                            val companyDto = getCompanyById((notification.fromUser as Company).id)

                            if (companyDto != null) {
                                notification.copy(
                                    fromUser = companyDto.toDomain()
                                )
                            } else {
                                notification
                            }
                        }
                    }
                }

            Result.Success(notifications)
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.INVALID_ARGUMENT -> Result.Error(NetworkError.INVALID_ARGUMENT)
                FirebaseFirestoreException.Code.NOT_FOUND -> Result.Error(NetworkError.NOT_FOUND)
                FirebaseFirestoreException.Code.ALREADY_EXISTS -> Result.Error(NetworkError.ALREADY_EXISTS)
                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> Result.Error(NetworkError.RESOURCE_EXHAUSTED)
                FirebaseFirestoreException.Code.ABORTED -> Result.Error(NetworkError.ABORTED)
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> Result.Error(NetworkError.DEADLINE_EXCEEDED)
                FirebaseFirestoreException.Code.UNKNOWN -> Result.Error(NetworkError.UNKNOWN)
                else -> Result.Error(NetworkError.UNKNOWN)
            }
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    private suspend fun getPersonById(id: String): PersonDto? {
        return db.collection(AppConstants.PERSONS)
            .document(id)
            .get()
            .await()
            .toObject(PersonDto::class.java)
    }

    private suspend fun getCompanyById(id: String): CompanyDto? {
        return db.collection(AppConstants.COMPANIES)
            .document(id)
            .get()
            .await()
            .toObject(CompanyDto::class.java)
    }
}