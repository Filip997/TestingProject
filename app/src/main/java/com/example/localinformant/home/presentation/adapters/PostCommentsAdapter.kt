package com.example.localinformant.home.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.presentation.models.CommentUi
import com.example.localinformant.core.presentation.util.loadImage
import com.example.localinformant.core.presentation.util.toTimeAgo
import com.example.localinformant.databinding.PostCommentAdapterDesignBinding

class PostCommentsAdapter(
    private val context: Context,
    private val goToUserProfile: (userId: String, userType: UserType) -> Unit
) : ListAdapter<CommentUi, PostCommentsAdapter.PostCommentViewHolder>(DiffCallback()) {

    class PostCommentViewHolder(binding: PostCommentAdapterDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        val userProfileImage = binding.civHomeUserNewCommentProfileImage
        val userName = binding.tvHomeUserNameNewComment
        val createdAt = binding.tvHomeCommentTimeSubmited
        val userCommentText = binding.tvHomeUserCommentText
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostCommentViewHolder {
        return PostCommentViewHolder(PostCommentAdapterDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(
        holder: PostCommentViewHolder,
        position: Int
    ) {
        val currentComment = getItem(position)

        loadImage(
            context = context,
            imageUrl = currentComment.userProfileImage,
            target = holder.userProfileImage
        )
        holder.userName.text = currentComment.userName
        holder.createdAt.text = currentComment.createdAt.toTimeAgo()
        holder.userCommentText.text = currentComment.commentText

        holder.userName.setOnClickListener {
            goToUserProfile.invoke(currentComment.userId, currentComment.userType!!)
        }

        holder.userProfileImage.setOnClickListener {
            goToUserProfile.invoke(currentComment.userId, currentComment.userType!!)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CommentUi>() {

        override fun areItemsTheSame(oldItem: CommentUi, newItem: CommentUi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CommentUi, newItem: CommentUi): Boolean {
            return oldItem == newItem
        }
    }
}