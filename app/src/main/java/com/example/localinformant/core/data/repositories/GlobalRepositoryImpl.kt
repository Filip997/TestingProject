package com.example.localinformant.core.data.repositories

import android.util.Log
import com.example.localinformant.core.data.api.FcmApi
import com.example.localinformant.core.data.constants.AppConstants
import com.example.localinformant.core.data.dto.CommentDto
import com.example.localinformant.core.data.dto.CompanyDto
import com.example.localinformant.core.data.dto.NotificationDto
import com.example.localinformant.core.data.dto.NotificationRequestDto
import com.example.localinformant.core.data.dto.PersonDto
import com.example.localinformant.core.data.dto.PostDto
import com.example.localinformant.core.data.dto.ReactionDto
import com.example.localinformant.core.data.mappers.toDomain
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Comment
import com.example.localinformant.core.domain.repositories.GlobalRepository
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.NotificationType
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.Post
import com.example.localinformant.core.domain.models.Reaction
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.network.NetworkChecker
import com.example.localinformant.core.domain.result.Result
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.collections.chunked

class GlobalRepositoryImpl @Inject constructor(
    private val networkChecker: NetworkChecker,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val fcmApi: FcmApi
) : GlobalRepository {

    override suspend fun updatePersonToken(token: String): Person? {
        return try {
            val currentUserId = auth.currentUser?.uid!!
            db.collection(AppConstants.PERSONS)
                .document(currentUserId)
                .update("token", token)
                .await()

            getPersonById(currentUserId)
        } catch (e: Exception) {
            Log.d("GlobalRepository", "Update token for person failed: ${e.toString()}")

            null
        }
    }

    override suspend fun updateCompanyToken(token: String): Company? {
        return try {
            val currentUserId = auth.currentUser?.uid!!
            db.collection(AppConstants.COMPANIES)
                .document(currentUserId)
                .update("token", token)
                .await()

            getCompanyById(currentUserId)
        } catch (e: Exception) {
            Log.d("GlobalRepository", "Update token for company failed: ${e.toString()}")

            null
        }
    }

    override suspend fun getPersonById(id: String): Person? {
        return db.collection(AppConstants.PERSONS)
            .document(id)
            .get()
            .await()
            .toObject(PersonDto::class.java)?.toDomain()
    }

    override suspend fun getCompanyById(id: String): Company? {
        return db.collection(AppConstants.COMPANIES)
                .document(id)
                .get()
                .await()
                .toObject(CompanyDto::class.java)?.toDomain()
    }

    override suspend fun observeUsersByIds(
        userTypeIds: Map<String, UserType>
    ): Flow<List<User>> = callbackFlow {

        if (userTypeIds.isEmpty()) {
            trySend(emptyList())
            return@callbackFlow
        }

        val listeners = mutableListOf<ListenerRegistration>()
        val usersMap = mutableMapOf<String, User>()

        val needsPersons = userTypeIds.containsValue(UserType.PERSON)
        val needsCompanies = userTypeIds.containsValue(UserType.COMPANY)

        val expectedListeners = buildSet {
            if (needsPersons) add(AppConstants.PERSONS)
            if (needsCompanies) add(AppConstants.COMPANIES)
        }

        val initializedListeners = mutableSetOf<String>()

        fun emitIfReady() {
            if (initializedListeners.containsAll(expectedListeners)) {
                trySend(usersMap.values.toList())
            }
        }

        val userIds = userTypeIds.keys

        userIds.chunked(10).forEach { chunk ->

            val personIds = chunk.filter {
                userTypeIds[it] == UserType.PERSON
            }

            val companyIds = chunk.filter {
                userTypeIds[it] == UserType.COMPANY
            }

            if (personIds.isNotEmpty()) {

                val listener = db.collection(AppConstants.PERSONS)
                    .whereIn("id", personIds)
                    .addSnapshotListener { snapshot, error ->

                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        snapshot?.documents
                            ?.mapNotNull {
                                it.toObject(PersonDto::class.java)?.toDomain()
                            }
                            ?.forEach { user ->
                                usersMap[user.id] = user
                            }

                        initializedListeners.add(AppConstants.PERSONS)

                        emitIfReady()
                    }

                listeners.add(listener)
            }

            if (companyIds.isNotEmpty()) {

                val listener = db.collection(AppConstants.COMPANIES)
                    .whereIn("id", companyIds)
                    .addSnapshotListener { snapshot, error ->

                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        snapshot?.documents
                            ?.mapNotNull {
                                it.toObject(CompanyDto::class.java)?.toDomain()
                            }
                            ?.forEach { user ->
                                usersMap[user.id] = user
                            }

                        initializedListeners.add(AppConstants.COMPANIES)

                        emitIfReady()
                    }

                listeners.add(listener)
            }
        }

        awaitClose {
            listeners.forEach { it.remove() }
        }
    }

    override suspend fun submitReaction(
        id: String,
        postId: String,
        user: User,
        userType: UserType
    ): Result<Post, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val currentUserId = auth.currentUser?.uid!!

            val reaction = ReactionDto(
                id = id,
                userId = currentUserId,
                userType = userType.name,
                postId = postId
            )

            db.collection(AppConstants.REACTIONS)
                .document(id)
                .set(reaction)
                .await()

            db.collection(AppConstants.POSTS)
                .document(postId)
                .update("likes", FieldValue.arrayUnion(id))

            val postResult = getPostById(postId)

            if (postResult is Result.Success) {
                Result.Success(postResult.data)
            } else {
                Result.Error(NetworkError.UNKNOWN)
            }
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.INVALID_ARGUMENT -> Result.Error(NetworkError.INVALID_ARGUMENT)
                FirebaseFirestoreException.Code.NOT_FOUND -> Result.Error(NetworkError.NOT_FOUND)
                FirebaseFirestoreException.Code.ALREADY_EXISTS -> Result.Error(NetworkError.ALREADY_EXISTS)
                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> Result.Error(NetworkError.RESOURCE_EXHAUSTED)
                FirebaseFirestoreException.Code.ABORTED -> Result.Error(NetworkError.ABORTED)
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> Result.Error(NetworkError.DEADLINE_EXCEEDED)
                FirebaseFirestoreException.Code.UNKNOWN -> Result.Error(NetworkError.UNKNOWN)
                else -> Result.Error(NetworkError.UNKNOWN)
            }
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    override fun observeReactions(postIds: List<String>): Flow<List<Reaction>> = callbackFlow {
        if (postIds.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listeners = mutableListOf<ListenerRegistration>()

        postIds.chunked(10).forEach { chunk ->
            val listener = db.collection(AppConstants.REACTIONS)
                .whereIn("postId", chunk)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val reactions = snapshot?.documents
                        ?.mapNotNull { it.toObject(ReactionDto::class.java) }
                        ?.map { it.toDomain() }
                        ?: emptyList()

                    trySend(reactions)
                }

            listeners.add(listener)
        }

        awaitClose {
            listeners.forEach { it.remove() }
        }
    }

    override suspend fun submitComment(
        id: String, postId: String, commentText: String, user: User, userType: UserType
    ): Result<Post, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val currentUserId = auth.currentUser?.uid!!

            val comment = CommentDto(
                id = id,
                createdAt = Timestamp.now(),
                userId = currentUserId,
                userType = userType.name,
                postId = postId,
                commentText = commentText
            )

            db.collection(AppConstants.COMMENTS)
                .document(id)
                .set(comment)
                .await()

            db.collection(AppConstants.POSTS)
                .document(postId)
                .update("comments", FieldValue.arrayUnion(id))

            val postResult = getPostById(postId)

            if (postResult is Result.Success) {
                Result.Success(postResult.data)
            } else {
                Result.Error(NetworkError.UNKNOWN)
            }
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.INVALID_ARGUMENT -> Result.Error(NetworkError.INVALID_ARGUMENT)
                FirebaseFirestoreException.Code.NOT_FOUND -> Result.Error(NetworkError.NOT_FOUND)
                FirebaseFirestoreException.Code.ALREADY_EXISTS -> Result.Error(NetworkError.ALREADY_EXISTS)
                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> Result.Error(NetworkError.RESOURCE_EXHAUSTED)
                FirebaseFirestoreException.Code.ABORTED -> Result.Error(NetworkError.ABORTED)
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> Result.Error(NetworkError.DEADLINE_EXCEEDED)
                FirebaseFirestoreException.Code.UNKNOWN -> Result.Error(NetworkError.UNKNOWN)
                else -> Result.Error(NetworkError.UNKNOWN)
            }
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    override fun observeComments(postIds: List<String>): Flow<List<Comment>> = callbackFlow {
        if (postIds.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listeners = mutableListOf<ListenerRegistration>()

        postIds.chunked(10).forEach { chunk ->
            val listener = db.collection(AppConstants.COMMENTS)
                .whereIn("postId", chunk)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val comments = snapshot?.documents
                        ?.mapNotNull { it.toObject(CommentDto::class.java) }
                        ?.map { it.toDomain() }
                        ?: emptyList()

                    trySend(comments)
                }

            listeners.add(listener)
        }

        awaitClose {
            listeners.forEach { it.remove() }
        }
    }

    private suspend fun getPostById(postId: String): Result<Post, NetworkError> {
        return try {
            val result = db.collection(AppConstants.POSTS)
                .document(postId)
                .get()
                .await()
                .toObject(PostDto::class.java)

            if (result != null) {
                Result.Success(result.toDomain())
            } else {
                Result.Error(NetworkError.NOT_FOUND)
            }
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.INVALID_ARGUMENT -> Result.Error(NetworkError.INVALID_ARGUMENT)
                FirebaseFirestoreException.Code.NOT_FOUND -> Result.Error(NetworkError.NOT_FOUND)
                FirebaseFirestoreException.Code.ALREADY_EXISTS -> Result.Error(NetworkError.ALREADY_EXISTS)
                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> Result.Error(NetworkError.RESOURCE_EXHAUSTED)
                FirebaseFirestoreException.Code.ABORTED -> Result.Error(NetworkError.ABORTED)
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> Result.Error(NetworkError.DEADLINE_EXCEEDED)
                FirebaseFirestoreException.Code.UNKNOWN -> Result.Error(NetworkError.UNKNOWN)
                else -> Result.Error(NetworkError.UNKNOWN)
            }
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    override suspend fun getUsersWhoCommentedByPostId(
        postId: String,
        postUserId: String
    ): Result<List<String>, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val currentUserId = auth.currentUser?.uid!!

            val userIds = db.collection(AppConstants.COMMENTS)
                .whereEqualTo("postId", postId)
                .get()
                .await()
                .documents
                .mapNotNull {
                    it.toObject(CommentDto::class.java)
                }
                .map {
                    it.userId
                }
                .filter {
                    it != currentUserId && it != postUserId
                }
                .distinct()

            Result.Success(userIds)
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.INVALID_ARGUMENT -> Result.Error(NetworkError.INVALID_ARGUMENT)
                FirebaseFirestoreException.Code.NOT_FOUND -> Result.Error(NetworkError.NOT_FOUND)
                FirebaseFirestoreException.Code.ALREADY_EXISTS -> Result.Error(NetworkError.ALREADY_EXISTS)
                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> Result.Error(NetworkError.RESOURCE_EXHAUSTED)
                FirebaseFirestoreException.Code.ABORTED -> Result.Error(NetworkError.ABORTED)
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> Result.Error(NetworkError.DEADLINE_EXCEEDED)
                FirebaseFirestoreException.Code.UNKNOWN -> Result.Error(NetworkError.UNKNOWN)
                else -> Result.Error(NetworkError.UNKNOWN)
            }
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    override suspend fun saveNotificationToDatabase(
        id: String,
        fromUserId: String,
        fromUserType: UserType,
        notificationType: NotificationType,
        postId: String
    ): Result<Unit, NetworkError> {
        return try {
            val currentUserId = auth.currentUser?.uid!!

            val notificationDto = NotificationDto(
                id = id,
                createdOn = Timestamp.now(),
                fromUserId = fromUserId,
                fromUserType = fromUserType.name,
                toUserId = currentUserId,
                notificationType = notificationType.name,
                postId = postId
            )

            db.collection(AppConstants.NOTIFICATIONS)
                .add(notificationDto)
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    override suspend fun sendNotification(
        toUserIds: List<String>,
        fromUserType: UserType,
        notificationType: NotificationType,
        postId: String,
        message: String
    ): Result<Unit, NetworkError> {
        return try {
            val currentUserId = auth.currentUser?.uid!!

            val request = NotificationRequestDto(
                toUserIds = toUserIds,
                fromUserId = currentUserId,
                fromUserType = fromUserType.name,
                notificationType = notificationType.name,
                postId = postId,
                message = message
            )

            val result = fcmApi.sendNotification(request)

            if (result.isSuccessful) {
                Log.d("SendNotification", "Successful")
                Result.Success(Unit)
            } else {
                Log.d("SendNotification", "Failed")
                Result.Error(NetworkError.UNKNOWN)
            }
        } catch (e: Exception) {
            Log.d("SendNotification", e.message.toString())
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}