package com.example.localinformant.core.domain.usecases

import com.example.localinformant.core.domain.repositories.PreferencesRepository
import javax.inject.Inject

class GetAppLanguageCodeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    operator fun invoke() = preferencesRepository.getAppLanguage()
}