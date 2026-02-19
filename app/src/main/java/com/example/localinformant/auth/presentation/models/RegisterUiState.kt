package com.example.localinformant.auth.presentation.models

import com.example.localinformant.auth.domain.error.ValidationError
import com.example.localinformant.core.domain.models.UserType

data class RegisterUiState(
    val userType: UserType? = null,
    val companyName: String? = null,
    val companyNameError: ValidationError? = ValidationError.EMPTY_FIELD,
    val companyEmail: String? = null,
    val companyEmailError: ValidationError? = ValidationError.EMPTY_FIELD,
    val firstName: String? = null,
    val firstNameError: ValidationError? = ValidationError.EMPTY_FIELD,
    val lastName: String? = null,
    val lastNameError: ValidationError? = ValidationError.EMPTY_FIELD,
    val email: String? = null,
    val emailError: ValidationError? = ValidationError.EMPTY_FIELD,
    val password: String? = null,
    val passwordError: ValidationError? = ValidationError.EMPTY_FIELD,
    val confirmPassword: String? = null,
    val confirmPasswordError: ValidationError? = ValidationError.EMPTY_FIELD,
    val isAgreementChecked: Boolean = false,
    val isRegistrationEnabled: Boolean = false,
    val isLoading: Boolean = false
)
