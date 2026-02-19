package com.example.localinformant.auth.domain.usecases

import com.example.localinformant.auth.domain.repositories.FirebaseAuthRepository
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.error.Error
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.models.Company
import com.example.localinformant.models.Person
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val authRepository: FirebaseAuthRepository,
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(email: String, password: String, userType: UserType): Result<User, Error> {
        return when(userType) {
            UserType.PERSON -> {
                when(val result = authRepository.loginPerson(email, password)) {
                    is Result.Success -> {
                        preferencesRepository.savePerson(result.data as Person)
                        result
                    }
                    is Result.Error -> result
                }
            }
            UserType.COMPANY -> {
                when(val result = authRepository.loginCompany(email, password)) {
                    is Result.Success -> {
                        preferencesRepository.saveCompany(result.data as Company)
                        result
                    }
                    is Result.Error -> result
                }
            }
        }
    }
}