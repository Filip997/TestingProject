package com.example.localinformant.core.presentation.util

import android.content.Context
import com.example.localinformant.R
import com.example.localinformant.core.domain.error.NetworkError

fun NetworkError.toString(context: Context): String {
    val resource = when (this) {
        NetworkError.NO_INTERNET_CONNECTION -> R.string.no_internet_connection
        NetworkError.USER_NOT_FOUND -> R.string.we_couldnt_find_an_account_with_those_details
        NetworkError.INVALID_ARGUMENT -> R.string.invalid_argument_message
        NetworkError.NOT_FOUND -> R.string.not_found_message
        NetworkError.ALREADY_EXISTS -> R.string.already_exists_message
        NetworkError.RESOURCE_EXHAUSTED -> R.string.too_many_attemts_please_wait_a_few_minutes_and_try_again
        NetworkError.ABORTED -> R.string.aborted_message
        NetworkError.DEADLINE_EXCEEDED -> R.string.deadline_exceeded_message
        NetworkError.UPLOAD_IMAGES_FAILED -> R.string.failed_uploading_images
        NetworkError.UNKNOWN -> R.string.something_unexpected_happened_please_try_again
    }

    return context.getString(resource)
}