package com.example.localinformant.language.domain

import com.example.localinformant.core.domain.PreferencesRepository
import javax.inject.Inject

class GetAppLanguageUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke() = preferencesRepository.getAppLanguage()
}