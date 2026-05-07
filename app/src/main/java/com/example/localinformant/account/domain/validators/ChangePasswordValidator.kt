package com.example.localinformant.account.domain.validators

import com.example.localinformant.auth.domain.validators.hasDigit
import com.example.localinformant.auth.domain.validators.hasSpecialCharacter
import com.example.localinformant.auth.domain.validators.hasUppercase
import com.example.localinformant.auth.domain.validators.hasWhiteSpace
import com.example.localinformant.auth.domain.validators.isShort
import com.example.localinformant.core.domain.error.ValidationError
import javax.inject.Inject

class ChangePasswordValidator @Inject constructor() {

    fun validateOldPassword(oldPassword: String): ValidationError? {
        return if (oldPassword.isEmpty()) {
            ValidationError.EMPTY_FIELD
        } else {
            null
        }
    }

    fun validateNewPassword(newPassword: String): ValidationError? {
        return if (newPassword.isEmpty()) {
            ValidationError.EMPTY_FIELD
        } else if (isShort(newPassword)) {
            ValidationError.PASSWORD_TOO_SHORT
        } else if (!hasSpecialCharacter(newPassword)) {
            ValidationError.NO_SPECIAL_CHARACTER
        } else if (!hasDigit(newPassword)) {
            ValidationError.NO_DIGIT
        } else if (!hasUppercase(newPassword)) {
            ValidationError.NO_UPPERCASE_LETTER
        } else if (hasWhiteSpace(newPassword)) {
            ValidationError.HAS_WHITESPACE
        } else {
            null
        }
    }
}