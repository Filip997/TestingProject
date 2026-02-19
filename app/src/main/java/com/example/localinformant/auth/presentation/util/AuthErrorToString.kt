package com.example.localinformant.auth.presentation.util

import android.content.Context
import com.example.localinformant.R
import com.example.localinformant.auth.domain.error.AuthError

fun AuthError.toString(context: Context): String {
    val resource = when (this) {
        AuthError.INVALID_CREDENTIALS -> R.string.the_email_or_password_you_entered_is_incorrect
        AuthError.USER_NOT_FOUND -> R.string.we_couldnt_find_an_account_with_those_details
        AuthError.USER_ALREADY_EXISTS -> R.string.an_account_with_this_email_already_exists
        AuthError.NETWORK_ERROR -> R.string.something_went_wrong_while_connecting_to_the_server
        AuthError.TOO_MANY_REQUESTS -> R.string.too_many_attemts_please_wait_a_few_minutes_and_try_again
        AuthError.EMAIL_NOT_VERIFIED -> R.string.please_verify_your_email_address_before_signing_in
        AuthError.NO_INTERNET_CONNECTION -> R.string.no_internet_connection
        AuthError.UNKNOWN -> R.string.something_unexpected_happened_please_try_again
    }

    return context.getString(resource)
}