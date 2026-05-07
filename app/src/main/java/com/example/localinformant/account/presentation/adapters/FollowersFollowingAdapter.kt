package com.example.localinformant.account.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.presentation.models.FollowerFollowingUserUi
import com.example.localinformant.core.presentation.util.loadImage
import com.example.localinformant.databinding.FollowersFollowingAdapterDesignBinding

class FollowersFollowingAdapter(
    private val context: Context,
    private val users: List<FollowerFollowingUserUi>,
    private val goToUserProfile: (String, UserType) -> Unit
) : RecyclerView.Adapter<FollowersFollowingAdapter.FollowersFollowingViewHolder>() {

    class FollowersFollowingViewHolder(binding: FollowersFollowingAdapterDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        val layoutUserReaction = binding.layoutHomeReactionUser
        val userProfileImage = binding.civAccountFollowerFollowingProfileImage
        val userName = binding.tvAccountFollowerFollowingName
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FollowersFollowingViewHolder {
        return FollowersFollowingViewHolder(
            FollowersFollowingAdapterDesignBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: FollowersFollowingViewHolder,
        position: Int
    ) {
        val currentUser = users[position]

        loadImage(
            context = context,
            imageUrl = currentUser.userProfileImage,
            target = holder.userProfileImage
        )

        holder.userName.text = currentUser.userName

        holder.layoutUserReaction.setOnClickListener {
            goToUserProfile.invoke(currentUser.userId, currentUser.userType!!)
        }
    }

    override fun getItemCount(): Int = users.size
}