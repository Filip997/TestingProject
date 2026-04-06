package com.example.localinformant.notifications.presentation.adapters

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.localinformant.R
import com.example.localinformant.core.domain.models.NotificationType
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.presentation.models.NotificationUi
import com.example.localinformant.core.presentation.util.loadImage
import com.example.localinformant.core.presentation.util.toTimeAgo
import com.example.localinformant.databinding.UserNotificationAdapterDesignBinding

class UserNotificationsAdapter(
    private val context: Context,
    private val goToUserProfile: (String, UserType) -> Unit,
    private val goToSpecificPost: (String) -> Unit
) : ListAdapter<NotificationUi, UserNotificationsAdapter.UserNotificationsViewHolder>(DiffCallback()) {

    class UserNotificationsViewHolder(
        val binding: UserNotificationAdapterDesignBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val layoutNotification = binding.layoutNotification
        val userProfileImageUrl = binding.civNotificationUserProfileImage
        val description = binding.tvNotificationDescription
        val createdOn = binding.tvNotificationCreatedOn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserNotificationsViewHolder {
        val binding = UserNotificationAdapterDesignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserNotificationsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserNotificationsViewHolder, position: Int) {
        val notification = getItem(position)

        loadImage(
            context = context,
            imageUrl = notification.fromUserProfileImageUrl,
            target = holder.userProfileImageUrl
        )

        val description = createNotificationDescription(notification)

        holder.description.text = description
        holder.description.movementMethod = LinkMovementMethod.getInstance()

        holder.createdOn.text = notification.createdOn.toTimeAgo()

        holder.layoutNotification.setOnClickListener {
            when(notification.notificationType) {
                NotificationType.NEW_LIKE -> goToSpecificPost.invoke(notification.postId)
                NotificationType.NEW_COMMENT -> goToSpecificPost.invoke(notification.postId)
                NotificationType.OTHER_PEOPLE_COMMENTED -> goToSpecificPost.invoke(notification.postId)
                NotificationType.NEW_FOLLOWER -> goToUserProfile.invoke(notification.fromUserId, notification.fromUserType!!)
                else -> {

                }
            }
        }
    }

    private fun createNotificationDescription(notification: NotificationUi): CharSequence {
        val userName = notification.fromUserName

        val descriptionText = when(notification.notificationType) {
            NotificationType.NEW_LIKE -> context.getString(R.string.new_like_notification_body, userName)
            NotificationType.NEW_COMMENT -> context.getString(R.string.new_comment_notification_body, userName)
            NotificationType.OTHER_PEOPLE_COMMENTED -> {
                return context.getString(R.string.other_people_commented_notification_body)
            }
            NotificationType.NEW_FOLLOWER -> context.getString(R.string.new_follower_notification_body, userName)
            else -> {
                return ""
            }
        }

        val spannableString = SpannableStringBuilder(descriptionText)

        val boldSpan = StyleSpan(Typeface.BOLD)
        val startIndex = descriptionText.indexOf(userName)
        val endIndex = startIndex + userName.length

        spannableString.setSpan(
            boldSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                goToUserProfile.invoke(notification.fromUserId, notification.fromUserType!!)
            }
        }

        spannableString.setSpan(
            clickableSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }

    class DiffCallback : DiffUtil.ItemCallback<NotificationUi>() {

        override fun areItemsTheSame(oldItem: NotificationUi, newItem: NotificationUi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NotificationUi, newItem: NotificationUi): Boolean {
            return oldItem == newItem
        }
    }
}