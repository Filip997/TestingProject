package com.example.localinformant.auth.domain.error

import com.example.localinformant.core.domain.error.Error

enum class AuthError : Error {

    INVALID_CREDENTIALS,
    USER_NOT_FOUND,
    USER_ALREADY_EXISTS,
    NETWORK_ERROR,
    TOO_MANY_REQUESTS,
    EMAIL_NOT_VERIFIED,
    NO_INTERNET_CONNECTION,
    UNKNOWN
}