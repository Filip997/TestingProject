package com.example.localinformant.core.presentation.util

import android.content.Context
import com.example.localinformant.R
import com.example.localinformant.core.domain.models.NotificationType

fun NotificationType.toTitle(context: Context): String {
    val res = when(this) {
        NotificationType.NEW_MESSAGE -> R.string.new_message
        NotificationType.NEW_LIKE -> R.string.new_like
        NotificationType.NEW_COMMENT -> R.string.new_comment
        NotificationType.OTHER_PEOPLE_COMMENTED -> R.string.new_comment
        NotificationType.NEW_FOLLOWER -> R.string.new_follower
    }

    return context.getString(res)
}

fun NotificationType.toBody(context: Context, userName: String): String {
    val res = when(this) {
        NotificationType.NEW_LIKE -> R.string.new_like_notification_body
        NotificationType.NEW_COMMENT -> R.string.new_comment_notification_body
        NotificationType.OTHER_PEOPLE_COMMENTED -> {
            return context.getString(R.string.other_people_commented_notification_body)
        }
        NotificationType.NEW_FOLLOWER -> R.string.new_follower_notification_body
        else -> return ""
    }

    return context.getString(res, userName)
}