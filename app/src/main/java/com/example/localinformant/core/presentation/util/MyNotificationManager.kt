package com.example.localinformant.core.presentation.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.localinformant.R
import com.example.localinformant.core.presentation.constants.NotificationConstants
import com.example.localinformant.splash.presentation.activities.SplashScreenActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class MyNotificationManager @Inject constructor(
    @param:ApplicationContext val context: Context
) {

    private val _unreadNotificationsCount = MutableStateFlow(0)
    val unreadNotificationsCount: StateFlow<Int> = _unreadNotificationsCount

    private val _unreadMessagesCount = MutableStateFlow(0)
    val unreadMessagesCount: StateFlow<Int> = _unreadMessagesCount

    fun incrementNotificationsCount() {
        _unreadNotificationsCount.value += 1
    }

    fun resetNotificationsCount() {
        _unreadNotificationsCount.value = 0
    }

    fun incrementMessagesCount() {
        _unreadMessagesCount.value += 1
    }

    fun resetMessagesCount() {
        _unreadMessagesCount.value = 0
    }

    suspend fun createNotification(title: String, message: String, profileImageUrl: String?) {
        withContext(Dispatchers.IO) {
            val intent = Intent(context, SplashScreenActivity::class.java)
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = Random.nextInt()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(notificationManager)
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )

            val profileImageBitmap: Bitmap? = profileImageUrl?.let { getBitmapFromUrl(it) }

            val notification =
                NotificationCompat.Builder(context, NotificationConstants.NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(profileImageBitmap ?: ContextCompat.getDrawable(context, R.drawable.default_profile_pic)?.toBitmap())
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .build()

            notificationManager.notify(notificationId, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NotificationConstants.NOTIFICATION_CHANNEL_ID,
            NotificationConstants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = NotificationConstants.NOTIFICATION_CHANNEL_DESCRIPTION
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }
}