package com.example.localinformant.notifications.presentation.models

import com.example.localinformant.core.presentation.models.NotificationUi

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationUi> = listOf()
)
