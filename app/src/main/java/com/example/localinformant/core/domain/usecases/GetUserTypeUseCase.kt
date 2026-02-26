package com.example.localinformant.core.domain.usecases

import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import javax.inject.Inject

class GetUserTypeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    operator fun invoke() = preferencesRepository.getUserType()?.let {
        UserType.valueOf(it)
    }
}