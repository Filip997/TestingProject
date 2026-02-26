package com.example.localinformant.main.domain.usecases

import android.net.Uri
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.repositories.FirebaseFirestoreRepository
import com.example.localinformant.core.domain.repositories.FirebaseStorageRepository
import com.example.localinformant.core.domain.result.Result
import javax.inject.Inject

class CreateAPostUseCase @Inject constructor(
    private val firebaseFirestoreRepository: FirebaseFirestoreRepository,
    private val storageRepository: FirebaseStorageRepository
) {

    suspend operator fun invoke(id: String, postText: String, uriImages: List<Uri>): Result<Unit, NetworkError> {
        return when(val storageResult = storageRepository.savePostImages(id, uriImages)) {
            is Result.Success -> {
                firebaseFirestoreRepository.savePost(
                    id, postText, storageResult.data
                )
            }
            is Result.Error -> storageResult
        }
    }
}