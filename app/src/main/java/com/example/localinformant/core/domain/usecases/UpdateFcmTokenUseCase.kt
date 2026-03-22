package com.example.localinformant.core.domain.usecases

import com.example.localinformant.core.domain.repositories.GlobalRepository
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import javax.inject.Inject

class UpdateFcmTokenUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val globalRepository: GlobalRepository
) {

    suspend operator fun invoke(token: String) {
        val person = preferencesRepository.getPerson()
        val company = preferencesRepository.getCompany()

        if (person != null) {
            globalRepository.updatePersonToken(token)?.let {
                preferencesRepository.savePerson(it)
            }
        } else if (company != null) {
            globalRepository.updateCompanyToken(token)?.let {
                preferencesRepository.saveCompany(it)
            }
        }
    }
}