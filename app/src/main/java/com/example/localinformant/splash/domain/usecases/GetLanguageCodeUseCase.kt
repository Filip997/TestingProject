package com.example.localinformant.splash.domain.usecases

import com.example.localinformant.core.domain.repositories.PreferencesRepository
import javax.inject.Inject

class GetLanguageCodeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke() = preferencesRepository.getAppLanguage()
}