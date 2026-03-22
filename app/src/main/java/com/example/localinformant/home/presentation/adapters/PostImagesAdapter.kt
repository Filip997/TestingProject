package com.example.localinformant.home.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.localinformant.R
import com.example.localinformant.core.presentation.util.loadImage
import com.example.localinformant.databinding.ItemPostImageAdapterDesignBinding

class PostImagesAdapter(
    private val context: Context,
    private val postImageUrls: List<String>
) : RecyclerView.Adapter<PostImagesAdapter.PostImagesViewHolder>() {

    class PostImagesViewHolder(binding: ItemPostImageAdapterDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        val postImage = binding.ivHomePost
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImagesViewHolder {
        return PostImagesViewHolder(
            ItemPostImageAdapterDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostImagesViewHolder, position: Int) {
        loadImage(
            context = context,
            imageUrl = postImageUrls[position],
            target = holder.postImage,
            placeholderImage = R.drawable.default_placeholder_image
        )
    }

    override fun getItemCount() = postImageUrls.size
}