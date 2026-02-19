package com.example.localinformant.auth.domain.validators

import java.util.regex.Pattern

const val PASSWORD_REGEX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[.,=;+()\"#?!@$%^&'_{}|/<>*~:`-]).{8,}$"

fun isShort(password: String): Boolean {
    return password.length < 6
}

fun hasSpecialCharacter(password: String): Boolean {
    return Pattern.compile(PASSWORD_REGEX).matcher(password).find()
}

fun hasDigit(password: String): Boolean {
    val digit = password.find { it.isDigit() }
    return digit != null
}

fun hasUppercase(password: String): Boolean {
    val uppercaseChar = password.find { it.isUpperCase() }
    return uppercaseChar != null
}

fun hasWhiteSpace(password: String): Boolean {
    val whiteSpace = password.find { it.isWhitespace() }
    return whiteSpace != null
}