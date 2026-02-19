package com.example.localinformant.setup.domain.usecases

import com.example.localinformant.core.domain.repositories.PreferencesRepository
import javax.inject.Inject

class GetAppLanguageUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke() = preferencesRepository.getAppLanguage()
}