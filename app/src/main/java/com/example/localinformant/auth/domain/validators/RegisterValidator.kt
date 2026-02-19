package com.example.localinformant.auth.domain.validators

import com.example.localinformant.auth.domain.error.ValidationError
import com.example.localinformant.core.domain.models.UserType
import javax.inject.Inject

class RegisterValidator @Inject constructor(

) {
    private var password = ""

    fun validateCompanyName(companyName: String): ValidationError? {
        return if (companyName.isEmpty()) {
            ValidationError.EMPTY_FIELD
        } else {
            null
        }
    }

    fun validateCompanyEmail(companyEmail: String): ValidationError? {
        return if (companyEmail.isEmpty()) {
            ValidationError.EMPTY_FIELD
        } else if (!companyEmail.matches(EMAIL_REGEX)) {
            ValidationError.INVALID_EMAIL_FORMAT
        } else {
            null
        }
    }

    fun validateFirstName(firstName: String): ValidationError? {
        return if (firstName.isEmpty()) {
            ValidationError.EMPTY_FIELD
        } else {
            null
        }
    }

    fun validateLastName(lastName: String): ValidationError? {
        return if (lastName.isEmpty()) {
            ValidationError.EMPTY_FIELD
        } else {
            null
        }
    }

    fun validateEmail(email: String): ValidationError? {
        return if (email.isEmpty()) {
            ValidationError.EMPTY_FIELD
        } else if (!email.matches(EMAIL_REGEX)) {
            ValidationError.INVALID_EMAIL_FORMAT
        } else {
            null
        }
    }

    fun validatePassword(password: String): ValidationError? {
        this.password = password

        return if (password.isEmpty()) {
            ValidationError.EMPTY_FIELD
        } else if (isShort(password)) {
            ValidationError.PASSWORD_TOO_SHORT
        } else if (!hasSpecialCharacter(password)) {
            ValidationError.NO_SPECIAL_CHARACTER
        } else if (!hasDigit(password)) {
            ValidationError.NO_DIGIT
        } else if (!hasUppercase(password)) {
            ValidationError.NO_UPPERCASE_LETTER
        } else if (hasWhiteSpace(password)) {
            ValidationError.HAS_WHITESPACE
        } else {
            null
        }
    }

    fun validateConfirmPassword(confirmPassword: String): ValidationError? {
        return if (confirmPassword.isEmpty()) {
            ValidationError.EMPTY_FIELD
        } else if (confirmPassword != password) {
            ValidationError.PASSWORDS_DONT_MATCH
        } else {
            null
        }
    }

    fun validateIfRegistrationIsEnabled(
        userType: UserType,
        companyNameError: ValidationError?,
        companyEmailError: ValidationError?,
        firstNameError: ValidationError?,
        lastNameError: ValidationError?,
        emailError: ValidationError?,
        passwordError: ValidationError?,
        confirmPasswordError: ValidationError?,
        isAgreementChecked: Boolean
    ): Boolean {
        return when(userType) {
            UserType.PERSON -> firstNameError == null && lastNameError == null
                    && emailError == null && passwordError == null
                    && confirmPasswordError == null && isAgreementChecked
            UserType.COMPANY -> companyNameError == null && companyEmailError == null
                    && firstNameError == null && lastNameError == null
                    && emailError == null && passwordError == null
                    && confirmPasswordError == null && isAgreementChecked
        }
    }

    companion object {
        private val EMAIL_REGEX =
            Regex("^[A-Za-z0-9+_.-]+@(.+)$")
    }
}