package com.example.localinformant.account.data.repositories

import android.net.Uri
import android.util.Log
import com.example.localinformant.account.domain.error.ChangePassError
import com.example.localinformant.account.domain.repositories.UserAccountRepository
import com.example.localinformant.core.data.constants.AppConstants
import com.example.localinformant.core.data.dto.CommentDto
import com.example.localinformant.core.data.dto.CompanyDto
import com.example.localinformant.core.data.dto.PersonDto
import com.example.localinformant.core.data.dto.PostDto
import com.example.localinformant.core.data.dto.ReactionDto
import com.example.localinformant.core.data.mappers.toDomain
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Comment
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.Post
import com.example.localinformant.core.domain.models.Reaction
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.network.NetworkChecker
import com.example.localinformant.core.domain.result.Result
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import kotlin.collections.chunked
import kotlin.collections.set

class UserAccountRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val networkChecker: NetworkChecker
) : UserAccountRepository {

    override suspend fun getUsersByIds(userIds: List<String>): Result<List<User>, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            if (userIds.isEmpty()) {
                return Result.Success(listOf())
            }

            val users = mutableListOf<User>()

            userIds
                .distinct()
                .chunked(10)
                .forEach { chunk ->

                    val personsTask = db.collection(AppConstants.PERSONS)
                        .whereIn("id", chunk)
                        .get()

                    val companiesTask = db.collection(AppConstants.COMPANIES)
                        .whereIn("id", chunk)
                        .get()

                    val personsSnapshot = personsTask.await()
                    val companiesSnapshot = companiesTask.await()

                    val persons = personsSnapshot.documents
                        .mapNotNull {
                            it.toObject(PersonDto::class.java)
                        }
                        .map {
                            it.toDomain()
                        }

                    val companies = companiesSnapshot.documents
                        .mapNotNull {
                            it.toObject(CompanyDto::class.java)
                        }
                        .map {
                            it.toDomain()
                        }

                    users += persons
                    users += companies
                }

            Result.Success(users)
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.INVALID_ARGUMENT -> return Result.Error(NetworkError.INVALID_ARGUMENT)
                FirebaseFirestoreException.Code.NOT_FOUND -> return Result.Error(NetworkError.NOT_FOUND)
                FirebaseFirestoreException.Code.ALREADY_EXISTS -> return Result.Error(NetworkError.ALREADY_EXISTS)
                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> return Result.Error(NetworkError.RESOURCE_EXHAUSTED)
                FirebaseFirestoreException.Code.ABORTED -> return Result.Error(NetworkError.ABORTED)
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> return Result.Error(NetworkError.DEADLINE_EXCEEDED)
                FirebaseFirestoreException.Code.UNKNOWN -> return Result.Error(NetworkError.UNKNOWN)
                else -> Result.Error(NetworkError.UNKNOWN)
            }
        } catch (e: Exception) {
            return Result.Error(NetworkError.UNKNOWN)
        }
    }

    override suspend fun saveProfileImage(
        imageUri: Uri,
        userType: UserType
    ): Result<String, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val currentUserId = auth.currentUser?.uid!!
            val userFolder = when(userType) {
                UserType.PERSON -> AppConstants.PERSONS
                UserType.COMPANY -> AppConstants.COMPANIES
            }

            val storageRef = storage.reference
                .child(userFolder)
                .child(currentUserId)
                .child(AppConstants.PROFILE_PICTURE)

            val existingFiles = storageRef.listAll().await()

            existingFiles.items.forEach { file ->
                file.delete().await()
            }

            val fileName = UUID.randomUUID().toString()
            val fileRef = storageRef.child("$fileName.jpg")

            fileRef.putFile(imageUri).await()
            val downloadUrl = fileRef.downloadUrl.await()

            Result.Success(downloadUrl.toString())
        } catch (e: StorageException) {
            Result.Error(NetworkError.UPLOAD_IMAGES_FAILED)
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    override suspend fun setProfileImage(
        imageUrl: String,
        userType: UserType
    ): Result<User, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val currentUserId = auth.currentUser?.uid!!

            val user = when(userType) {
                UserType.PERSON -> {
                    db.collection(AppConstants.PERSONS)
                        .document(currentUserId)
                        .update("profileImageUrl", imageUrl)
                        .await()

                    getPersonById(currentUserId)
                }
                UserType.COMPANY -> {
                    db.collection(AppConstants.COMPANIES)
                        .document(currentUserId)
                        .update("companyProfileImageUrl", imageUrl)
                        .await()

                    getCompanyById(currentUserId)
                }
            }

            if (user != null) {
                Result.Success(user)
            } else {
                Result.Error(NetworkError.UNKNOWN)
            }
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.INVALID_ARGUMENT -> return Result.Error(NetworkError.INVALID_ARGUMENT)
                FirebaseFirestoreException.Code.NOT_FOUND -> return Result.Error(NetworkError.NOT_FOUND)
                FirebaseFirestoreException.Code.ALREADY_EXISTS -> return Result.Error(NetworkError.ALREADY_EXISTS)
                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> return Result.Error(NetworkError.RESOURCE_EXHAUSTED)
                FirebaseFirestoreException.Code.ABORTED -> return Result.Error(NetworkError.ABORTED)
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> return Result.Error(NetworkError.DEADLINE_EXCEEDED)
                FirebaseFirestoreException.Code.UNKNOWN -> return Result.Error(NetworkError.UNKNOWN)
                else -> Result.Error(NetworkError.UNKNOWN)
            }
        } catch (e: Exception) {
            return Result.Error(NetworkError.UNKNOWN)
        }
    }

    override suspend fun followCompany(
        currentUserType: UserType,
        userId: String
    ): Result<User, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val collection = when(currentUserType) {
                UserType.PERSON -> AppConstants.PERSONS
                UserType.COMPANY -> AppConstants.COMPANIES
            }

            val currentUserId = auth.currentUser?.uid!!

            db.collection(collection)
                .document(currentUserId)
                .update("following", FieldValue.arrayUnion(userId))
                .await()

            db.collection(AppConstants.COMPANIES)
                .document(userId)
                .update("followers", FieldValue.arrayUnion(currentUserId))
                .await()

            val currentUser = when(currentUserType) {
                UserType.PERSON -> getPersonById(currentUserId)
                UserType.COMPANY -> getCompanyById(currentUserId)
            }

            if (currentUser != null) {
                Result.Success(currentUser)
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

    override suspend fun unfollowCompany(
        currentUserType: UserType,
        userId: String
    ): Result<User, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val collection = when(currentUserType) {
                UserType.PERSON -> AppConstants.PERSONS
                UserType.COMPANY -> AppConstants.COMPANIES
            }

            val currentUserId = auth.currentUser?.uid!!

            db.collection(collection)
                .document(currentUserId)
                .update("following", FieldValue.arrayRemove(userId))
                .await()

            db.collection(AppConstants.COMPANIES)
                .document(userId)
                .update("followers", FieldValue.arrayRemove(currentUserId))
                .await()

            val currentUser = when(currentUserType) {
                UserType.PERSON -> getPersonById(currentUserId)
                UserType.COMPANY -> getCompanyById(currentUserId)
            }

            if (currentUser != null) {
                Result.Success(currentUser)
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

    private suspend fun getPersonById(id: String): Person? {
        return db.collection(AppConstants.PERSONS)
            .document(id)
            .get()
            .await()
            .toObject(PersonDto::class.java)?.toDomain()
    }

    private suspend fun getCompanyById(id: String): Company? {
        return db.collection(AppConstants.COMPANIES)
            .document(id)
            .get()
            .await()
            .toObject(CompanyDto::class.java)?.toDomain()
    }

    private var lastDocument: DocumentSnapshot? = null

    override suspend fun getPostsByUserId(userId: String, isRefreshing: Boolean): Result<List<Post>, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val currentUserId = auth.currentUser?.uid!!
            val allPosts = mutableListOf<Post>()

            if (isRefreshing) {
                lastDocument = null
            }

            var query = db.collection(AppConstants.POSTS)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(5)

            lastDocument?.let { lastSnapshot ->
                query = query.startAfter(lastSnapshot)
            }

            val snapshot = query.get().await()

            val postsDto = snapshot.documents
                .mapNotNull { it.toObject(PostDto::class.java) }

            if (snapshot.documents.isNotEmpty()) {
                lastDocument = snapshot.documents.last()
            }

            val posts = postsDto.map { it.toDomain() }
            allPosts.addAll(posts)

            val finalPosts = allPosts
                .map { post ->
                    val reactionsResult = getReactionsByPostId(post.id)

                    if (reactionsResult is Result.Success) {
                        val reactionsMap = reactionsResult.data.associateBy { it.id }

                        post.copy(
                            likes = post.likes.map { reactionsMap[it.id] ?: it }
                        )
                    } else post
                }
                .map { post ->
                    val commentsResult = getCommentsByPostId(post.id)

                    if (commentsResult is Result.Success) {
                        val commentsMap = commentsResult.data.associateBy { it.id }

                        post.copy(
                            comments = post.comments.map { commentsMap[it.id] ?: it }
                        )
                    } else post
                }
                .map { post ->
                    post.copy(
                        likeBtnClicked = post.likes.any { it.userId == currentUserId }
                    )
                }

            Result.Success(finalPosts)
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

    override suspend fun getPostIdsFromUserReactions(userId: String): Result<List<String>, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val postIds = db.collection(AppConstants.REACTIONS)
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .documents
                .mapNotNull {
                    it.toObject(ReactionDto::class.java)
                }
                .map {
                    it.postId
                }
                .distinct()

            Result.Success(postIds)
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

    override suspend fun getPostIdsFromUserComments(userId: String): Result<List<String>, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val postIds = db.collection(AppConstants.COMMENTS)
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .documents
                .mapNotNull {
                    it.toObject(CommentDto::class.java)
                }
                .map {
                    it.postId
                }
                .distinct()

            Result.Success(postIds)
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

    private var lastPosts: HashMap<Int, DocumentSnapshot>? = null

    override suspend fun getPostsByPostIds(
        postIds: List<String>,
        isRefreshing: Boolean
    ): Result<List<Post>, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            if (postIds.isEmpty()) {
                return Result.Success(listOf())
            }

            val currentUserId = auth.currentUser?.uid!!

            val postIdChunks = postIds.chunked(10)
            val allPosts = mutableListOf<Post>()

            if (isRefreshing || lastPosts == null) {
                lastPosts = HashMap(postIdChunks.size)
            }

            for ((chunkIndex, postIdChunk) in postIdChunks.withIndex()) {
                var query = db.collection(AppConstants.POSTS)
                    .whereIn("id", postIdChunk)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(5)

                lastPosts?.get(chunkIndex)?.let { lastSnapshot ->
                    query = query.startAfter(lastSnapshot)
                }

                val snapshot = query.get().await()

                val postsDto = snapshot.documents
                    .mapNotNull { it.toObject(PostDto::class.java) }

                if (snapshot.documents.isNotEmpty()) {
                    lastPosts?.set(chunkIndex, snapshot.documents.last())
                }

                val posts = postsDto.map { it.toDomain() }
                allPosts.addAll(posts)
            }

            val sortedPosts = allPosts
                .sortedByDescending { it.createdAt }

            val finalPosts = sortedPosts
                .map { post ->
                    val reactionsResult = getReactionsByPostId(post.id)

                    if (reactionsResult is Result.Success) {
                        val reactionsMap = reactionsResult.data.associateBy { it.id }

                        post.copy(
                            likes = post.likes.map { reactionsMap[it.id] ?: it }
                        )
                    } else post
                }
                .map { post ->
                    val commentsResult = getCommentsByPostId(post.id)

                    if (commentsResult is Result.Success) {
                        val commentsMap = commentsResult.data.associateBy { it.id }

                        post.copy(
                            comments = post.comments.map { commentsMap[it.id] ?: it }
                        )
                    } else post
                }
                .map { post ->
                    post.copy(
                        likeBtnClicked = post.likes.any { it.userId == currentUserId }
                    )
                }

            Result.Success(finalPosts)
        } catch (e: FirebaseFirestoreException) {
            Log.d("UserAccountRepository", e.message.toString())

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
            Log.d("UserAccountRepository", e.message.toString())

            Result.Error(NetworkError.UNKNOWN)
        }
    }

    override suspend fun getCompanyByPostId(postId: String): Result<Company, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val company = db.collection(AppConstants.COMPANIES)
                .whereArrayContains("posts", postId)
                .get()
                .await()
                .documents
                .mapNotNull {
                    it.toObject(CompanyDto::class.java)
                }
                .map { it.toDomain() }

            Result.Success(company.first())
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

    private suspend fun getReactionsByPostId(postId: String): Result<List<Reaction>, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val reactions = db.collection(AppConstants.REACTIONS)
                .whereEqualTo("postId", postId)
                .get()
                .await()
                .documents
                .mapNotNull {
                    it.toObject(ReactionDto::class.java)
                }
                .map {
                    it.toDomain()
                }

            Result.Success(reactions)
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

    private suspend fun getCommentsByPostId(postId: String): Result<List<Comment>, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val comments = db.collection(AppConstants.COMMENTS)
                .whereEqualTo("postId", postId)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .await()
                .documents
                .mapNotNull {
                    it.toObject(CommentDto::class.java)
                }
                .map {
                    it.toDomain()
                }

            Result.Success(comments)
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

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): Result<Unit, ChangePassError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(ChangePassError.NO_INTERNET_CONNECTION)
            }

            val user = auth.currentUser
            val email = user?.email ?: return Result.Error(ChangePassError.UNKNOWN)

            val credential = EmailAuthProvider.getCredential(email, oldPassword)

            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()

            Result.Success(Unit)
        } catch (e: Exception) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> Result.Error(ChangePassError.INVALID_OLD_PASSWORD)
                else -> Result.Error(ChangePassError.UNKNOWN)
            }
        }
    }

    override suspend fun logout(): Result<Unit, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            auth.signOut()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}