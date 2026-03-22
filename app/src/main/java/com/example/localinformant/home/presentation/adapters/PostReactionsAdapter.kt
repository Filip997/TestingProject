package com.example.localinformant.home.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.presentation.models.ReactionUi
import com.example.localinformant.core.presentation.util.loadImage
import com.example.localinformant.databinding.ItemReactionAdapterDesignBinding

class PostReactionsAdapter(
    private val context: Context,
    private val reactions: List<ReactionUi>,
    private val goToUserProfile: (String, UserType) -> Unit
) : RecyclerView.Adapter<PostReactionsAdapter.PostReactionsViewHolder>() {

    class PostReactionsViewHolder(binding: ItemReactionAdapterDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        val layoutUserReaction = binding.layoutHomeReactionUser
        val userProfileImage = binding.civHomeReactedUserProfileImage
        val userName = binding.tvHomeReactedUserName
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostReactionsViewHolder {
        return PostReactionsViewHolder(
            ItemReactionAdapterDesignBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: PostReactionsViewHolder,
        position: Int
    ) {
        val currentReaction = reactions[position]

        loadImage(
            context = context,
            imageUrl = currentReaction.userProfileImage,
            target = holder.userProfileImage
        )

        holder.userName.text = currentReaction.userName

        holder.layoutUserReaction.setOnClickListener {
            goToUserProfile.invoke(currentReaction.userId, currentReaction.userType!!)
        }
    }

    override fun getItemCount(): Int = reactions.size
}