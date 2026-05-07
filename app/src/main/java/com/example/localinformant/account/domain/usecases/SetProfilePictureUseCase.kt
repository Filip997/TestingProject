package com.example.localinformant.account.domain.usecases

import android.net.Uri
import com.example.localinformant.account.domain.repositories.UserAccountRepository
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.result.Result
import javax.inject.Inject

class SetProfilePictureUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val userAccountRepository: UserAccountRepository
) {

    suspend operator fun invoke(imageUri: Uri): Result<String, NetworkError> {
        val userType = preferencesRepository.getUserType()?.let { UserType.valueOf(it) }

        return if (userType != null) {
            when(val storageResult = userAccountRepository.saveProfileImage(imageUri, userType)) {
                is Result.Success -> {
                    val imageUrl = storageResult.data

                    when(val result = userAccountRepository.setProfileImage(imageUrl, userType)) {
                        is Result.Success -> {
                            when(val user = result.data) {
                                is Person -> preferencesRepository.savePerson(user)
                                is Company -> preferencesRepository.saveCompany(user)
                            }

                            Result.Success(imageUrl)
                        }
                        is Result.Error -> result
                    }
                }
                is Result.Error -> storageResult
            }
        } else {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}