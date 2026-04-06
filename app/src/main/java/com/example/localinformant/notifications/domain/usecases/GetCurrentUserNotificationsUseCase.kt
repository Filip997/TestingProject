package com.example.localinformant.notifications.domain.usecases

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Notification
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.notifications.domain.repositories.NotificationsRepository
import javax.inject.Inject

class GetCurrentUserNotificationsUseCase @Inject constructor(
    private val notificationsRepository: NotificationsRepository
) {

    suspend operator fun invoke(): Result<List<Notification>, NetworkError> {
        return notificationsRepository.getUserNotifications()
    }
}