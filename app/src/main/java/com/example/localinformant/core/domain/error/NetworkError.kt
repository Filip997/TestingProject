package com.example.localinformant.core.domain.error

enum class NetworkError : Error {
    NO_INTERNET_CONNECTION,
    USER_NOT_FOUND,
    INVALID_ARGUMENT,
    NOT_FOUND,
    ALREADY_EXISTS,
    RESOURCE_EXHAUSTED,
    ABORTED,
    DEADLINE_EXCEEDED,
    UPLOAD_IMAGES_FAILED,
    UNKNOWN
}