package com.example.localinformant.notifications.domain.repositories

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Notification
import com.example.localinformant.core.domain.result.Result

interface NotificationsRepository {

    suspend fun getUserNotifications(): Result<List<Notification>, NetworkError>
}