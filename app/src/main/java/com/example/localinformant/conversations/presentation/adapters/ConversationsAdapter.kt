package com.example.localinformant.conversations.presentation.adapters

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.localinformant.R
import com.example.localinformant.core.presentation.models.ConversationUi
import com.example.localinformant.core.presentation.util.loadImage
import com.example.localinformant.core.presentation.util.toTimeAgo
import com.example.localinformant.databinding.ConversationsAdapterDesignBinding

class ConversationsAdapter(
    private val context: Context,
    private val onConversationClick: (String, String) -> Unit
) : ListAdapter<ConversationUi, ConversationsAdapter.ConversationViewHolder>(DiffCallback()) {

    private var unreadMessagesUserIds: List<String>? = null

    class ConversationViewHolder(
        binding: ConversationsAdapterDesignBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val layoutContainer = binding.layoutConversationsContainer
        val userProfileImage = binding.civConversationsUserProfileImage
        val userName = binding.tvConversationsUserName
        val lastMessage = binding.tvConversationsLastMessage
        val timestamp = binding.tvConversationsLastMessageTime
        val unreadMessageCircle = binding.unreadMessageCircleHome
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConversationViewHolder {
        return ConversationViewHolder(
            ConversationsAdapterDesignBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ConversationViewHolder,
        position: Int
    ) {
        val currentConversation = getItem(position)

        loadImage(context, currentConversation.participant2ProfileImage, holder.userProfileImage)
        holder.userName.text = currentConversation.participant2Name

        val lastMessage = currentConversation.lastMessage
        val spannableString = SpannableStringBuilder(lastMessage)
        val startIndex = 0
        val endIndex = lastMessage.length

        if (unreadMessagesUserIds?.contains(currentConversation.participant2Id) == true) {
            holder.unreadMessageCircle.visibility = View.VISIBLE

            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            holder.unreadMessageCircle.visibility = View.GONE

            spannableString.setSpan(
                StyleSpan(Typeface.NORMAL),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        val finalLastMessageText = if (currentConversation.lastMessageUserId == currentConversation.participant2Id) {
            spannableString
        } else {
            "${context.getString(R.string.you)}: $spannableString"
        }

        holder.lastMessage.text = finalLastMessageText

        if (currentConversation.lastMessageTime > 0) {
            holder.timestamp.text = currentConversation.lastMessageTime.toTimeAgo()
        }

        holder.layoutContainer.setOnClickListener {
            onConversationClick(
                currentConversation.participant2Id,
                currentConversation.id
            )
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ConversationUi>() {

        override fun areItemsTheSame(oldItem: ConversationUi, newItem: ConversationUi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ConversationUi, newItem: ConversationUi): Boolean {
            return oldItem == newItem
        }
    }

    fun setUnreadMessagesUserIds(userIds: List<String>) {
        unreadMessagesUserIds = userIds
        notifyDataSetChanged()
    }
}