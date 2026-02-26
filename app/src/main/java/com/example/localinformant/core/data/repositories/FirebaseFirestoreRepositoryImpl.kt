package com.example.localinformant.core.data.repositories

import android.util.Log
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.core.data.dto.PostDto
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.network.NetworkChecker
import com.example.localinformant.core.domain.repositories.FirebaseFirestoreRepository
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.models.Company
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseFirestoreRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val networkChecker: NetworkChecker
) : FirebaseFirestoreRepository {

    override suspend fun savePost(
        id: String,
        postText: String,
        imageUrls: List<String>
    ): Result<Unit, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val currentUserId = auth.currentUser?.uid!!
            val currentCompany = db.collection(AppConstants.COMPANIES).document(currentUserId).get().await().toObject(Company::class.java)

            if (currentCompany != null) {
                val post = PostDto(
                    id = id,
                    userId = currentUserId,
                    postText = postText,
                    imageUrls = imageUrls,
                    likes = listOf(),
                    comments = listOf()
                )

                db.collection(AppConstants.POSTS).document(id).set(post).await()

                Result.Success(Unit)
            } else {
                Result.Error(NetworkError.USER_NOT_FOUND)
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

    override suspend fun updatePersonToken(token: String) {
        try {
            val currentUserId = auth.currentUser?.uid!!
            db.collection(AppConstants.PERSONS)
                .document(currentUserId)
                .update("token", token)
                .await()
        } catch (e: Exception) {
            Log.d("FirebaseFirestoreRepository", "Update token for person failed: ${e.toString()}")
        }
    }

    override suspend fun updateCompanyToken(token: String) {
        try {
            val currentUserId = auth.currentUser?.uid!!
            db.collection(AppConstants.COMPANIES)
                .document(currentUserId)
                .update("token", token)
                .await()
        } catch (e: Exception) {
            Log.d("FirebaseFirestoreRepository", "Update token for company failed: ${e.toString()}")
        }
    }
}