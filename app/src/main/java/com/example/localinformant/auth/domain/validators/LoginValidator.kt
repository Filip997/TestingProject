package com.example.localinformant.auth.domain.validators

import com.example.localinformant.auth.domain.error.ValidationError
import javax.inject.Inject

class LoginValidator @Inject constructor() {

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
        return if (password.isEmpty()) {
            ValidationError.EMPTY_FIELD
        }  else {
            null
        }
    }

    companion object {
        private val EMAIL_REGEX =
            Regex("^[A-Za-z0-9+_.-]+@(.+)$")
    }
}