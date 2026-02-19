package com.example.localinformant.setup.domain.usecases

import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.models.UserType
import javax.inject.Inject

class SaveUserTypeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    operator fun invoke(userType: UserType) {
        preferencesRepository.saveUserType(userType.name)
    }
}