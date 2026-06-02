package com.example.localinformant.conversations.presentation.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.localinformant.core.presentation.models.MessageUi
import com.example.localinformant.core.presentation.util.loadImage
import com.example.localinformant.core.presentation.util.toChatDate
import com.example.localinformant.databinding.ChatAdapterDesignBinding
import com.example.localinformant.databinding.ItemMoreMessagesLoadingDesignBinding

class ChatAdapter(
    private val context: Context
) : ListAdapter<MessageUi, RecyclerView.ViewHolder>(DiffCallback()) {

    private var participant2UserId: String? = null
    private var participant2ProfileImage: String? = null
    private var isLoadingMore = false

    companion object {
        private const val VIEW_TYPE_MESSAGE = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    class ChatViewHolder(binding: ChatAdapterDesignBinding) : RecyclerView.ViewHolder(binding.root) {
        val layoutContainerLeft = binding.layoutChatMessageLeft
        val leftMessageTimeSent = binding.tvChatLeftMessageTime
        val leftContainerSpace = binding.chatMessageLeftSpace
        val leftUserProfileImage = binding.civChatLeftUserProfileImage
        val leftUserMessage = binding.tvChatLeftUserMessage

        val layoutContainerRight = binding.layoutChatMessageRight
        val rightMessageTimeSent = binding.tvChatRightMessageTime
        val rightContainerSpace = binding.chatMessageRightSpace
        val rightUserMessage = binding.tvChatRightUserMessage
    }

    class LoadingViewHolder(
        binding: ItemMoreMessagesLoadingDesignBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (position < currentList.size) {
            VIEW_TYPE_MESSAGE
        } else {
            VIEW_TYPE_LOADING
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_MESSAGE -> ChatViewHolder(
                ChatAdapterDesignBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            VIEW_TYPE_LOADING -> LoadingViewHolder(
                ItemMoreMessagesLoadingDesignBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when(holder) {
            is ChatViewHolder -> {
                val previousMessage = if (position > 0) getItem(position - 1) else null
                val currentMessage = getItem(position)
                val nextMessage = if (position < currentList.size - 1) getItem(position + 1) else null

                Log.d("ChatAdapter", "Previous message: $previousMessage position: $position")
                Log.d("ChatAdapter", "Current message: $currentMessage position: $position")
                Log.d("ChatAdapter", "Next message: $nextMessage, position: $position")

                if (currentMessage.senderId != participant2UserId) {
                    holder.layoutContainerLeft.visibility = View.GONE
                    holder.layoutContainerRight.visibility = View.VISIBLE

                    if (nextMessage == null || (currentMessage.timeSent - nextMessage.timeSent > 3600000)) {
                        holder.rightMessageTimeSent.visibility = View.VISIBLE
                        holder.rightMessageTimeSent.text = currentMessage.timeSent.toChatDate(context)
                    } else {
                        holder.rightMessageTimeSent.visibility = View.GONE
                    }

                    if (previousMessage == null || previousMessage.senderId == participant2UserId) {
                        holder.rightContainerSpace.visibility = View.VISIBLE
                    } else {
                        holder.rightContainerSpace.visibility = View.GONE
                    }

                    holder.rightUserMessage.text = currentMessage.content
                } else {
                    holder.layoutContainerRight.visibility = View.GONE
                    holder.layoutContainerLeft.visibility = View.VISIBLE

                    if (nextMessage == null || (currentMessage.timeSent - nextMessage.timeSent > 3600000)) {
                        holder.leftMessageTimeSent.visibility = View.VISIBLE
                        holder.leftMessageTimeSent.text = currentMessage.timeSent.toChatDate(context)
                    } else {
                        holder.leftMessageTimeSent.visibility = View.GONE
                    }

                    if (previousMessage == null || previousMessage.senderId != participant2UserId) {
                        holder.leftContainerSpace.visibility = View.VISIBLE
                    } else {
                        holder.leftContainerSpace.visibility = View.GONE
                    }

                    if (previousMessage == null
                        || previousMessage.senderId != currentMessage.senderId
                        || (previousMessage.timeSent - currentMessage.timeSent > 3600000)
                    ) {
                        holder.leftUserProfileImage.visibility = View.VISIBLE
                    } else {
                        holder.leftUserProfileImage.visibility = View.INVISIBLE
                    }

                    loadImage(context, participant2ProfileImage ?: "", holder.leftUserProfileImage)
                    holder.leftUserMessage.text = currentMessage.content
                }
            }
            is LoadingViewHolder -> {

            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size + if (isLoadingMore) 1 else 0
    }

    fun setParticipant2UserId(userId: String) {
        participant2UserId = userId
    }

    fun setParticipant2ProfileImage(profileImage: String) {
        participant2ProfileImage = profileImage
    }

    class DiffCallback : DiffUtil.ItemCallback<MessageUi>() {

        override fun areItemsTheSame(oldItem: MessageUi, newItem: MessageUi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MessageUi, newItem: MessageUi): Boolean {
            return oldItem == newItem
        }
    }

    fun showLoading() {
        if (!isLoadingMore) {
            isLoadingMore = true
            notifyItemInserted(currentList.size)
        }
    }

    fun hideLoading() {
        if (isLoadingMore) {
            isLoadingMore = false
            notifyItemRemoved(currentList.size)
        }
    }
}