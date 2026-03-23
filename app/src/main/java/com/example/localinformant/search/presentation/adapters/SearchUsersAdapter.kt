package com.example.localinformant.search.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.presentation.models.SearchedUserUi
import com.example.localinformant.core.presentation.util.loadImage
import com.example.localinformant.databinding.SearchUsersAdapterDesignBinding

class SearchUsersAdapter(
    private val context: Context,
    private val goToUserProfile: (String, UserType) -> Unit
) : ListAdapter<SearchedUserUi, SearchUsersAdapter.SearchUserViewHolder>(DiffCallback()) {

    class SearchUserViewHolder(binding: SearchUsersAdapterDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        val layoutSearchedUser = binding.layoutSearchUser
        val profilePicture = binding.civSearchUserProfileImage
        val userName = binding.tvSearchUserName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUserViewHolder {
        return SearchUserViewHolder(
            SearchUsersAdapterDesignBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SearchUserViewHolder, position: Int) {
        val currentUser = getItem(position)

        loadImage(
            context = context,
            imageUrl = currentUser.userProfileImageUrl,
            target = holder.profilePicture
        )

        holder.userName.text = currentUser.userName

        holder.layoutSearchedUser.setOnClickListener {
            goToUserProfile.invoke(currentUser.id, currentUser.userType!!)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SearchedUserUi>() {

        override fun areItemsTheSame(oldItem: SearchedUserUi, newItem: SearchedUserUi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SearchedUserUi, newItem: SearchedUserUi): Boolean {
            return oldItem == newItem
        }
    }
}