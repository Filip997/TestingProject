package com.example.localinformant.core.data.repositories

import android.net.Uri
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.network.NetworkChecker
import com.example.localinformant.core.domain.repositories.FirebaseStorageRepository
import com.example.localinformant.core.domain.result.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirebaseStorageRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val networkChecker: NetworkChecker
) : FirebaseStorageRepository {

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
}