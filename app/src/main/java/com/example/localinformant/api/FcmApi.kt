package com.example.localinformant.api

import com.example.localinformant.constants.FirebaseApiConstants
import com.example.localinformant.constants.FirebaseApiConstants.PROJECT_ID
import com.example.localinformant.models.NotificationBodyDto
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FcmApi {

    @Headers("Authorization: Bearer ${FirebaseApiConstants.API_KEY}", "Content-Type:${FirebaseApiConstants.CONTENT_TYPE}")
    @POST("projects/$PROJECT_ID/messages:send")
    suspend fun sendNotification(
        @Body notificationBody: NotificationBodyDto
    )
}