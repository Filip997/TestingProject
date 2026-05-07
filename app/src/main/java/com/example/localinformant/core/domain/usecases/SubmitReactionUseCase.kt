package com.example.localinformant.core.domain.usecases

import android.util.Log
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.NotificationType
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.Post
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.repositories.GlobalRepository
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.result.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.UUID
import javax.inject.Inject

class SubmitReactionUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val globalRepository: GlobalRepository
) {

    private var sendNotificationJob: Job? = null

    suspend operator fun invoke(postId: String): Result<Post, NetworkError> {
        sendNotificationJob?.cancel()

        val userType = UserType.valueOf(preferencesRepository.getUserType()!!)
        val reactionId = UUID.randomUUID().toString()

        val person = preferencesRepository.getPerson()
        val company = preferencesRepository.getCompany()

        val user: User = when(userType) {
            UserType.PERSON -> person!!
            UserType.COMPANY -> company!!
        }

        return when(val result = globalRepository.submitReaction(reactionId, postId, user, userType)) {
            is Result.Success -> {
                val post = result.data

                sendNotificationJob = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                    try {
                        withTimeout(8000L) {
                            if (user is Person || (user is Company && user.id != post.userId)) {
                                globalRepository.sendNotification(
                                    toUserIds = listOf(post.userId),
                                    fromUserType = userType,
                                    notificationType = NotificationType.NEW_LIKE,
                                    postId = post.id,
                                    message = ""
                                )
                            }
                        }
                    } catch (e: TimeoutCancellationException) {
                        Log.d("SubmitReactionUseCase", e.message.toString())
                    }
                }

                result
            }
            is Result.Error -> {
                result
            }
        }
    }
}