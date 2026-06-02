package com.example.localinformant.core.presentation.util

import android.content.Context
import com.example.localinformant.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Long.toTimeAgo(): String {

    val now = System.currentTimeMillis()
    val diff = now - this

    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)
    val days = diff / (1000 * 60 * 60 * 24)
    val weeks = diff / (1000 * 60 * 60 * 24 * 7)

    return when {
        minutes < 1 -> "now"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        days < 7 -> "${days}d"
        else -> "${weeks}w"
    }
}

fun Long.toChatDate(context: Context): String {
    val messageCalendar = Calendar.getInstance().apply {
        timeInMillis = this@toChatDate
    }

    val nowCalendar = Calendar.getInstance()

    val today = context.getString(R.string.today)
    val yesterday = context.getString(R.string.yesterday)
    val at = context.getString(R.string.at)

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("MMM dd '$at' HH:mm", Locale.getDefault())

    return when {
        isSameDay(messageCalendar, nowCalendar) -> {
            "$today $at ${timeFormat.format(Date(this))}"
        }

        isYesterday(messageCalendar, nowCalendar) -> {
            "$yesterday $at ${timeFormat.format(Date(this))}"
        }

        else -> {
            dateFormat.format(Date(this))
        }
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun isYesterday(messageCal: Calendar, nowCal: Calendar): Boolean {
    val yesterday = nowCal.clone() as Calendar
    yesterday.add(Calendar.DAY_OF_YEAR, -1)

    return isSameDay(messageCal, yesterday)
}