package com.example.localinformant.splash.domain.usecases

import com.example.localinformant.core.domain.repositories.PreferencesRepository
import javax.inject.Inject

class IsUserLoggedInUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    operator fun invoke(): Boolean {
        val person = preferencesRepository.getPerson()
        val company = preferencesRepository.getCompany()

        return person != null || company != null
    }
}