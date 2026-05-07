package com.example.localinformant.home.domain.usecases

import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import javax.inject.Inject

class GetCurrentUserProfileImageUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    operator fun invoke(): String {
        val userType = preferencesRepository.getUserType()?.let { UserType.valueOf(it) }

        return if (userType != null) {
            when(userType) {
                UserType.PERSON -> {
                    val person = preferencesRepository.getPerson()
                    person?.profileImageUrl ?: ""
                }
                UserType.COMPANY -> {
                    val company = preferencesRepository.getCompany()
                    company?.companyProfileImageUrl ?: ""
                }
            }
        } else {
            ""
        }
    }
}