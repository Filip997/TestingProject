package com.example.localinformant.repositories

import com.example.localinformant.constants.AppConstants
import com.example.localinformant.models.Notification
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NotificationRepository {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun sendNotification(userId: String, notification: Notification) {
        withContext(Dispatchers.IO) {
            try {
                db.collection(AppConstants.NOTIFICATIONS).document().set(notification).await()
            } catch (e: Exception) {

            }
        }
    }

    suspend fun getAllNotificationsFromCurrentUser(): List<Notification> =
        withContext(Dispatchers.IO) {
            val currentUserId = auth.currentUser?.uid ?: return@withContext listOf()

            try {
                db.collection(AppConstants.NOTIFICATIONS)
                    .whereEqualTo("toUserId", currentUserId)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { it.toObject(Notification::class.java) }
            } catch (e: Exception) {
                listOf()
            }
        }
}