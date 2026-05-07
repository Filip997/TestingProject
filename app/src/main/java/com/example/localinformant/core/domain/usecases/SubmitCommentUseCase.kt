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

class SubmitCommentUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val globalRepository: GlobalRepository
) {

    private var sendNotificationJob: Job? = null

    suspend operator fun invoke(postId: String, commentText: String): Result<Post, NetworkError> {
        sendNotificationJob?.cancel()

        val userType = UserType.valueOf(preferencesRepository.getUserType()!!)
        val commentId = UUID.randomUUID().toString()

        val person = preferencesRepository.getPerson()
        val company = preferencesRepository.getCompany()

        val user: User = when(userType) {
            UserType.PERSON -> person!!
            UserType.COMPANY -> company!!
        }

        return when(val result = globalRepository.submitComment(commentId, postId, commentText, user, userType)) {
            is Result.Success -> {
                val post = result.data

                sendNotificationJob = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                    try {
                        withTimeout(20000L) {
                            if (user is Person || (user is Company && user.id != post.userId)) {
                                Log.d(
                                    "SubmitCommentUseCase",
                                    "Other users with comments result: ${post.userId}"
                                )

                                globalRepository.sendNotification(
                                    toUserIds = listOf(post.userId),
                                    fromUserType = userType,
                                    notificationType = NotificationType.NEW_COMMENT,
                                    postId = post.id,
                                    message = ""
                                )
                            }

                            val otherUsersWithCommentsResult =
                                globalRepository.getUsersWhoCommentedByPostId(
                                    postId,
                                    post.userId
                                )

                            if (otherUsersWithCommentsResult is Result.Success) {
                                val otherUsersWithComments = otherUsersWithCommentsResult.data

                                Log.d(
                                    "SubmitCommentUseCase",
                                    "Other users with comments result: $otherUsersWithComments"
                                )

                                if (otherUsersWithComments.isNotEmpty()) {
                                    globalRepository.sendNotification(
                                        toUserIds = otherUsersWithComments,
                                        fromUserType = userType,
                                        notificationType = NotificationType.OTHER_PEOPLE_COMMENTED,
                                        postId = post.id,
                                        message = ""
                                    )
                                }
                            }
                        }
                    } catch (e: TimeoutCancellationException) {
                        Log.d("SubmitCommentUseCase", e.message.toString())
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