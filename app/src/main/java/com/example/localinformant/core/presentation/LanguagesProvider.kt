package com.example.localinformant.core.presentation

import android.content.Context
import com.example.localinformant.R
import com.example.localinformant.core.domain.models.Language

fun getLanguages(context: Context) = listOf(
    Language(
        context.getString(R.string.macedonian),
        "mk"
    ),
    Language(
        context.getString(R.string.english),
        "en"
    )
)