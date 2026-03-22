package com.example.localinformant.core.presentation.util

fun Long.toTimeAgo(): String {

    val now = System.currentTimeMillis()
    val diff = now - this

    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)
    val days = diff / (1000 * 60 * 60 * 24)

    return when {
        minutes < 1 -> "now"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        else -> "${days}d"
    }
}