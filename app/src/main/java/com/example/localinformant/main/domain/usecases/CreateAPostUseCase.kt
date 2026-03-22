package com.example.localinformant.main.domain.usecases

import android.net.Uri
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.main.domain.repositories.MainRepository
import javax.inject.Inject

class CreateAPostUseCase @Inject constructor(
    private val mainRepository: MainRepository,
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(id: String, postText: String, uriImages: List<Uri>): Result<Company, NetworkError> {
        return when(val storageResult = mainRepository.savePostImages(id, uriImages)) {
            is Result.Success -> {
                when(val result = mainRepository.savePost(
                    id, postText, storageResult.data
                )) {
                    is Result.Success -> {
                        preferencesRepository.saveCompany(result.data)
                        result
                    }
                    is Result.Error -> result
                }
            }
            is Result.Error -> storageResult
        }
    }
}