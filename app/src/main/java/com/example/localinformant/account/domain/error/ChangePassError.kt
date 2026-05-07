package com.example.localinformant.account.domain.error

import com.example.localinformant.core.domain.error.Error

enum class ChangePassError : Error {
    NO_INTERNET_CONNECTION,
    INVALID_OLD_PASSWORD,
    UNKNOWN
}