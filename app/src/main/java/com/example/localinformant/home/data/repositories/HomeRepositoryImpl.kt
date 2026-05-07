package com.example.localinformant.home.data.repositories

import com.example.localinformant.core.data.constants.AppConstants
import com.example.localinformant.core.data.dto.CommentDto
import com.example.localinformant.core.data.dto.CompanyDto
import com.example.localinformant.core.data.dto.PostDto
import com.example.localinformant.core.data.dto.ReactionDto
import com.example.localinformant.core.data.mappers.toDomain
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Comment
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Post
import com.example.localinformant.core.domain.models.Reaction
import com.example.localinformant.core.domain.network.NetworkChecker
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.home.domain.repositories.HomeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.jvm.java

class HomeRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val networkChecker: NetworkChecker
) : HomeRepository {

    private var lastPosts: HashMap<Int, DocumentSnapshot>? = null

    override suspend fun getPostsByFollowedCompanies(
        followedUsersIds: List<String>,
        isRefreshing: Boolean
    ): Result<List<Post>, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            if (followedUsersIds.isEmpty()) {
                return Result.Success(listOf())
            }

            val currentUserId = auth.currentUser?.uid!!

            val userChunks = followedUsersIds.chunked(10)
            val allPosts = mutableListOf<Post>()

            if (isRefreshing || lastPosts == null) {
                lastPosts = HashMap(userChunks.size)
            }

            for ((chunkIndex, userChunk) in userChunks.withIndex()) {
                var query = db.collection(AppConstants.POSTS)
                    .whereIn("userId", userChunk)
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

    override suspend fun getPostById(postId: String): Result<Post, NetworkError> {
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
}