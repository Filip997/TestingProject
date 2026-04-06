package com.example.localinformant.core.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.localinformant.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

private val requestOptions = RequestOptions().centerCrop()

fun loadImage(context: Context, imageUrl: String, target: ImageView, placeholderImage: Int = R.drawable.default_profile_pic) {
    Glide.with(context).load(imageUrl)
        .placeholder(placeholderImage)
        .apply(requestOptions)
        .into(target)
}

suspend fun getBitmapFromUrl(url: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection()
            connection.connect()
            val input = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            null
        }
    }
}