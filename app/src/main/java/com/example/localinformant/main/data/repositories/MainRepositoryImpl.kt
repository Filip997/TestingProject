package com.example.localinformant.main.data.repositories

import android.net.Uri
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.core.data.dto.CompanyDto
import com.example.localinformant.core.data.dto.PostDto
import com.example.localinformant.core.data.mappers.toDomain
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.network.NetworkChecker
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.main.domain.repositories.MainRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val networkChecker: NetworkChecker
) : MainRepository {

    override suspend fun savePostImages(postId: String, uriImages: List<Uri>): Result<List<String>, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val currentUserId = auth.currentUser?.uid!!

            val storageRef = storage.reference
                .child(AppConstants.COMPANIES)
                .child(currentUserId)
                .child(AppConstants.POSTS)
                .child(postId)

            val downloadUrls = mutableListOf<String>()

            for (uri in uriImages) {

                val fileName = UUID.randomUUID().toString()
                val fileRef = storageRef.child("$fileName.jpg")

                fileRef.putFile(uri).await()
                val downloadUrl = fileRef.downloadUrl.await()

                downloadUrls.add(downloadUrl.toString())
            }

            Result.Success(downloadUrls)
        } catch (e: StorageException) {
            Result.Error(NetworkError.UPLOAD_IMAGES_FAILED)
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    override suspend fun savePost(
        id: String,
        postText: String,
        imageUrls: List<String>
    ): Result<Company, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val currentUserId = auth.currentUser?.uid!!
            var currentCompany = getCompanyById(currentUserId)

            if (currentCompany != null) {
                val post = PostDto(
                    id = id,
                    createdAt = Timestamp.now(),
                    userId = currentUserId,
                    postText = postText,
                    imageUrls = imageUrls,
                    likes = listOf(),
                    comments = listOf()
                )

                currentCompany = currentCompany.copy(
                    posts = currentCompany.posts + post.id
                )

                db.collection(AppConstants.POSTS).document(id).set(post).await()
                db.collection(AppConstants.COMPANIES)
                    .document(currentUserId)
                    .update("posts", FieldValue.arrayUnion(id))
                    .await()

                Result.Success(currentCompany.toDomain())
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

    private suspend fun getCompanyById(id: String): CompanyDto? {
        return db.collection(AppConstants.COMPANIES)
            .document(id)
            .get()
            .await()
            .toObject(CompanyDto::class.java)
    }
}