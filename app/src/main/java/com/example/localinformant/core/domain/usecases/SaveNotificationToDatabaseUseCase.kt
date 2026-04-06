package com.example.localinformant.core.domain.usecases

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.NotificationType
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.repositories.GlobalRepository
import com.example.localinformant.core.domain.result.Result
import java.util.UUID
import javax.inject.Inject

class SaveNotificationToDatabaseUseCase @Inject constructor(
    private val globalRepository: GlobalRepository
) {

    suspend operator fun invoke(
        fromUserId: String,
        fromUserType: UserType,
        notificationType: NotificationType,
        postId: String
    ): Result<Unit, NetworkError> {
        val notificationId = UUID.randomUUID().toString()

        return globalRepository.saveNotificationToDatabase(
            id = notificationId,
            fromUserId = fromUserId,
            fromUserType = fromUserType,
            notificationType = notificationType,
            postId = postId
        )
    }
}