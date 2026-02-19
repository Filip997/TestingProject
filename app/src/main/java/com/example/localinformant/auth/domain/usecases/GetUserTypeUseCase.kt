package com.example.localinformant.auth.domain.usecases

import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.models.UserType
import javax.inject.Inject

class GetUserTypeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    operator fun invoke() = UserType.valueOf(preferencesRepository.getUserType()!!)
}