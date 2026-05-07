package com.example.localinformant.core.domain.error

enum class ValidationError {

    EMPTY_FIELD,
    INVALID_EMAIL_FORMAT,
    PASSWORD_TOO_SHORT,
    NO_SPECIAL_CHARACTER,
    NO_DIGIT,
    NO_UPPERCASE_LETTER,
    HAS_WHITESPACE,
    PASSWORDS_DONT_MATCH
}