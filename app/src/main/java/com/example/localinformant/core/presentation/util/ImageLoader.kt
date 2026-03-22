package com.example.localinformant.core.presentation.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.localinformant.R

private val requestOptions = RequestOptions().centerCrop()

fun loadImage(context: Context, imageUrl: String, target: ImageView, placeholderImage: Int = R.drawable.default_profile_pic) {
    Glide.with(context).load(imageUrl)
        .placeholder(placeholderImage)
        .apply(requestOptions)
        .into(target)
}