package com.example.localinformant.core.data.fcm

import android.util.Log
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.NotificationType
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.usecases.GetAppLanguageCodeUseCase
import com.example.localinformant.core.domain.usecases.GetUserByIdUseCase
import com.example.localinformant.core.domain.usecases.SaveNotificationToDatabaseUseCase
import com.example.localinformant.core.domain.usecases.UpdateFcmTokenUseCase
import com.example.localinformant.core.presentation.util.AppStateManager
import com.example.localinformant.core.presentation.util.MyNotificationManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    @Inject lateinit var appStateManager: AppStateManager
    @Inject lateinit var myNotificationManager: MyNotificationManager
    @Inject lateinit var updateFcmTokenUseCase: UpdateFcmTokenUseCase
    @Inject lateinit var saveNotificationToDatabaseUseCase: SaveNotificationToDatabaseUseCase
    @Inject lateinit var getUserByIdUseCase: GetUserByIdUseCase
    @Inject lateinit var getAppLanguageCodeUseCase: GetAppLanguageCodeUseCase

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        serviceScope.launch {
            updateFcmTokenUseCase.invoke(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val dataMessage = message.data

        Log.d("MyFirebaseMessagingService", dataMessage.toMap().toString())

        val fromUserId = dataMessage["userId"] ?: ""
        val fromUserType = dataMessage["userType"]?.let {
            runCatching { UserType.valueOf(it) }.getOrNull()
        } ?: return
        val notificationType = dataMessage["notificationType"]?.let {
            runCatching { NotificationType.valueOf(it) }.getOrNull()
        } ?: return
        val postId = dataMessage["postId"] ?: ""
        val messageText = dataMessage["message"] ?: ""

        if (notificationType != NotificationType.NEW_MESSAGE) {
            serviceScope.launch {
                saveNotificationToDatabaseUseCase.invoke(
                    fromUserId = fromUserId,
                    fromUserType = fromUserType,
                    notificationType = notificationType,
                    postId = postId
                )
            }
        }

        if (!appStateManager.isAppInForeground()) {
            serviceScope.launch {
                val user = getUserByIdUseCase.invoke(fromUserId, fromUserType)

                val userName = user?.let {
                    when (it) {
                        is Person -> it.fullName
                        is Company -> it.companyName
                        else -> ""
                    }
                } ?: ""

                val userProfileImageUrl = user?.let {
                    when (it) {
                        is Person -> it.profileImageUrl
                        is Company -> it.companyProfileImageUrl
                        else -> null
                    }
                }

                myNotificationManager.createNotification(
                    notificationType = notificationType,
                    userName = userName,
                    profileImageUrl = userProfileImageUrl,
                    messageText = messageText
                )
            }
        }

        when(notificationType) {
            NotificationType.NEW_MESSAGE -> myNotificationManager.incrementMessagesCount(fromUserId)
            else -> myNotificationManager.incrementNotificationsCount()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}