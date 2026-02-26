package com.example.localinformant.core.domain.usecases

import com.example.localinformant.core.domain.repositories.FirebaseFirestoreRepository
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import javax.inject.Inject

class UpdateFcmTokenUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val firestoreRepository: FirebaseFirestoreRepository
) {

    suspend operator fun invoke(token: String) {
        val person = preferencesRepository.getPerson()
        val company = preferencesRepository.getCompany()

        if (person != null) {
            firestoreRepository.updatePersonToken(token)
        } else if (company != null) {
            firestoreRepository.updateCompanyToken(token)
        }
    }
}