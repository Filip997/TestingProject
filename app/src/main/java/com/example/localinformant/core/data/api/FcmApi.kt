package com.example.localinformant.core.data.api

import com.example.localinformant.core.data.dto.NotificationRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmApi {

    @POST("api/notifications/send")
    suspend fun sendNotification(
        @Body notificationRequest: NotificationRequestDto
    ): Response<Unit>
}