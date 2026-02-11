package com.example.localinformant.splash.domain

import com.example.localinformant.core.domain.PreferencesRepository
import javax.inject.Inject

class GetLanguageCodeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke() = preferencesRepository.getAppLanguage()
}