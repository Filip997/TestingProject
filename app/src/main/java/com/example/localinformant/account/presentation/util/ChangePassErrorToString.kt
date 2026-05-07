package com.example.localinformant.account.presentation.util

import android.content.Context
import com.example.localinformant.R
import com.example.localinformant.account.domain.error.ChangePassError

fun ChangePassError.toString(context: Context): String {
    return when (this) {
        ChangePassError.NO_INTERNET_CONNECTION -> context.getString(R.string.no_internet_connection)
        ChangePassError.INVALID_OLD_PASSWORD -> context.getString(R.string.wrong_old_password)
        ChangePassError.UNKNOWN -> context.getString(R.string.something_unexpected_happened_please_try_again)
    }
}