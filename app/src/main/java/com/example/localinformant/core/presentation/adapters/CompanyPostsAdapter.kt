package com.example.localinformant.core.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.localinformant.R
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.presentation.models.PostUiState
import com.example.localinformant.core.presentation.models.ReactionUi
import com.example.localinformant.core.presentation.util.loadImage
import com.example.localinformant.core.presentation.util.toTimeAgo
import com.example.localinformant.databinding.CompanyPostsAdapterDesignBinding
import com.example.localinformant.databinding.ItemMorePostsLoadingDesignBinding
import com.example.localinformant.home.presentation.adapters.PostCommentsAdapter
import com.example.localinformant.home.presentation.adapters.PostImagesAdapter

class CompanyPostsAdapter(
    private val context: Context,
    private val goToUserProfile: (String, UserType) -> Unit,
    private val submitReaction: (String) -> Unit,
    private val openReactions: (List<ReactionUi>) -> Unit,
    private val onToggleComments: (String) -> Unit,
    private val submitComment: (String, String) -> Unit
) : ListAdapter<PostUiState, RecyclerView.ViewHolder>(DiffCallback()) {

    private var isLoadingMore = false

    companion object {
        private const val VIEW_TYPE_POST = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    class CompanyPostsViewHolder(
        private val goToUserProfile: (userId: String, userType: UserType) -> Unit,
        binding: CompanyPostsAdapterDesignBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val context: Context = binding.root.context

        var postCommentsAdapter: PostCommentsAdapter =
            PostCommentsAdapter(context, { userId, userType ->
                goToUserProfile.invoke(userId, userType)
            })

        init {
            binding.rvHomePostComments.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = postCommentsAdapter
            }
        }

        val companyPostProfileImage = binding.civHomeCompanyProfileImage
        val companyPostName = binding.tvHomeCompanyName
        val postCreatedAt = binding.tvHomePostTimeSubmited
        val companyPostText = binding.tvHomeCompanyPostText
        val layoutPostImages = binding.layoutHomePostImages
        val postImagesViewPager = binding.viewPagerHomePostImages
        val postImagesDotsLayout = binding.layoutHomeDots
        val postLikeBtn = binding.ivHomeLikeBtn
        val postNumLikes = binding.tvHomeNumLikes
        val layoutComment = binding.layoutHomeComment
        val postNumComments = binding.tvHomeNumComments
        val layoutCommentsSection = binding.layoutHomeCommentsSection
        val userCommentText = binding.tilHomeUserNewComment
        val commentTextEt = binding.tilHomeUserNewComment.editText
        val submitCommentBtn = binding.ivHomeSubmitBtn
    }

    class LoadingViewHolder(
        val binding: ItemMorePostsLoadingDesignBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (position < currentList.size) {
            VIEW_TYPE_POST
        } else {
            VIEW_TYPE_LOADING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_POST -> {
                val binding = CompanyPostsAdapterDesignBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                CompanyPostsViewHolder(
                    goToUserProfile = goToUserProfile,
                    binding = binding
                )
            }

            VIEW_TYPE_LOADING -> {
                val binding = ItemMorePostsLoadingDesignBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                LoadingViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CompanyPostsViewHolder -> {
                val currentPostUiState = getItem(position)

                loadImage(
                    context = context,
                    imageUrl = currentPostUiState.companyProfileImageUrl,
                    target = holder.companyPostProfileImage
                )

                holder.companyPostName.text = currentPostUiState.companyName
                holder.companyPostName.setOnClickListener {
                    goToUserProfile.invoke(currentPostUiState.companyId, UserType.COMPANY)
                }

                holder.postCreatedAt.text = currentPostUiState.createdAt.toTimeAgo()
                holder.companyPostText.text = currentPostUiState.postText

                if (currentPostUiState.postImageUrls.isNotEmpty()) {
                    holder.layoutPostImages.visibility = View.VISIBLE

                    setupPostImagesAdapter(
                        holder.postImagesViewPager,
                        holder.postImagesDotsLayout,
                        currentPostUiState.postImageUrls
                    )
                }

                holder.postLikeBtn.setImageResource(
                    if (currentPostUiState.likeBtnClicked) {
                        R.drawable.ic_like_active
                    } else {
                        R.drawable.ic_like
                    }
                )
                holder.postLikeBtn.setOnClickListener {
                    if (!currentPostUiState.likeBtnClicked) {
                        submitReaction.invoke(currentPostUiState.id)
                    }
                }

                holder.postNumLikes.text = currentPostUiState.postLikes.size.toString()
                holder.postNumLikes.setOnClickListener {
                    openReactions.invoke(currentPostUiState.postLikes)
                }

                holder.layoutComment.setOnClickListener {
                    onToggleComments.invoke(currentPostUiState.id)
                }
                holder.postNumComments.text = currentPostUiState.postComments.size.toString()
                holder.layoutCommentsSection.visibility = if (currentPostUiState.commentSectionVisible) View.VISIBLE else View.GONE

                holder.postCommentsAdapter.submitList(currentPostUiState.postComments)

                holder.submitCommentBtn.setOnClickListener {
                    if (!holder.commentTextEt?.text.isNullOrEmpty()) {
                        submitComment.invoke(
                            currentPostUiState.id,
                            holder.userCommentText.editText?.text.toString()
                        )

                        holder.commentTextEt.setText("")
                    }
                }
            }

            is LoadingViewHolder -> {

            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size + if (isLoadingMore) 1 else 0
    }

    private fun setupPostImagesAdapter(viewPager: ViewPager2, dotsLayout: LinearLayout, postImageUrls: List<String>) {
        viewPager.adapter = PostImagesAdapter(context, postImageUrls)

        viewPager.post {
            if (postImageUrls.size > 1) {
                setupDots(dotsLayout, postImageUrls.size, 0)
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (postImageUrls.size > 1) {
                    setupDots(dotsLayout, postImageUrls.size, position)
                }
            }
        })
    }

    private fun setupDots(dotsLayout: LinearLayout, postImagesSize: Int, activeIndex: Int) {
        dotsLayout.removeAllViews()
        val dots: Array<ImageView?> = arrayOfNulls(postImagesSize)

        for (i in 0 until postImagesSize) {
            dots[i] = ImageView(context).apply {
                val params = LinearLayout.LayoutParams(16, 16).also {
                    it.setMargins(8, 0, 8, 0)
                }

                layoutParams = params

                setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        if (i == activeIndex) R.drawable.dot_active
                        else R.drawable.dot_inactive
                    )
                )
            }

            dotsLayout.addView(dots[i])
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PostUiState>() {

        override fun areItemsTheSame(oldItem: PostUiState, newItem: PostUiState): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PostUiState, newItem: PostUiState): Boolean {
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