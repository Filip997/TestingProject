package com.example.localinformant.account.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localinformant.R
import com.example.localinformant.account.presentation.viewmodels.UserAccountViewModel
import com.example.localinformant.core.presentation.constants.IntentKeys
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.presentation.adapters.CompanyPostsAdapter
import com.example.localinformant.core.presentation.navigator.ScreensNavigator
import com.example.localinformant.core.presentation.util.loadImage
import com.example.localinformant.databinding.FragmentUserAccountBinding
import com.example.localinformant.home.presentation.util.ReactionsPopUpWindow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.view.isGone
import com.example.localinformant.account.presentation.events.FollowUnfollowCompanyEvent
import com.example.localinformant.account.presentation.events.OpenFollowersFollowingPopUpWindowEvent
import com.example.localinformant.account.presentation.events.SetProfilePictureEvent
import com.example.localinformant.account.presentation.events.StartConversationEvent
import com.example.localinformant.account.presentation.util.FollowersFollowingPopUpWindow
import com.example.localinformant.core.presentation.dialogs.CustomInfoDialog
import com.example.localinformant.core.presentation.util.toString
import com.example.localinformant.home.presentation.events.SubmitCommentEvent

@AndroidEntryPoint
class UserAccountFragment : Fragment() {

    private var _binding: FragmentUserAccountBinding? = null
    private val binding get() = _binding!!

    private val userAccountViewModel: UserAccountViewModel by viewModels()

    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var followersFollowingPopUpWindow: FollowersFollowingPopUpWindow
    @Inject lateinit var reactionsPopUpWindow: ReactionsPopUpWindow

    private lateinit var bundle: Bundle
    private var userId: String? = null
    private var userType: UserType? = null
    private lateinit var companyPostsAdapter: CompanyPostsAdapter

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                userAccountViewModel.setProfilePicture(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserAccountBinding.inflate(inflater, container, false)

        if (arguments != null) {
            userId = arguments?.getString(IntentKeys.USER_ID)
            userType = arguments?.getString(IntentKeys.USER_TYPE)?.let { UserType.valueOf(it) }

            if (userId != null && userType != null) {
                userAccountViewModel.getUserAccountDetails(userId, userType)
            } else {
                userAccountViewModel.getUserAccountDetails()
            }
        } else {
            userAccountViewModel.getUserAccountDetails()
        }

        bundle = Bundle()

        setupViewModels()
        setupRecyclerViewPosts()
        setupClickListeners()

        return binding.root
    }

    private fun setupViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userAccountViewModel.userAccountUiState.collect { state ->
                    if (state.error == null) {

                        if (state.isLoading)
                            binding.progressbarAccount.visibility = View.VISIBLE
                        else
                            binding.progressbarAccount.visibility = View.GONE

                        if (state.isLoadingFollowRequest)
                            binding.progressbarFollowRequestAccount.visibility = View.VISIBLE
                        else
                            binding.progressbarFollowRequestAccount.visibility = View.GONE

                        when (state.userAccountDetails.userType) {
                            UserType.PERSON -> {
                                binding.layoutUserFollowersAccount.visibility = View.GONE
                                binding.tglbtnFollowAccount.visibility = View.GONE
                                binding.layoutPersonReactionsCommentsAccount.visibility = View.VISIBLE
                            }

                            UserType.COMPANY -> {
                                binding.layoutUserFollowersAccount.visibility = View.VISIBLE
                                binding.tglbtnFollowAccount.visibility = View.VISIBLE
                                binding.layoutPersonReactionsCommentsAccount.visibility = View.GONE
                            }

                            null -> {

                            }
                        }

                        binding.tvFollowersNumAccount.text = state.userAccountDetails.followers.size.toString()
                        binding.tvFollowingNumAccount.text = state.userAccountDetails.following.size.toString()

                        loadImage(
                            context = requireContext(),
                            imageUrl = state.userAccountDetails.userProfileImage,
                            target = binding.ivUserProfilePictureAccount
                        )

                        binding.tvUserFullNameAccount.text = state.userAccountDetails.userName

                        if (state.userAccountDetails.isCurrentUser) {
                            binding.ivBackArrowAccount.visibility = View.GONE
                            binding.ivSettingsAccount.visibility = View.VISIBLE
                            binding.layoutEditProfilePictureAccount.visibility = View.VISIBLE
                            binding.layoutButtonsAccount.visibility = View.GONE
                        } else {
                            binding.ivBackArrowAccount.visibility = View.VISIBLE
                            binding.ivSettingsAccount.visibility = View.GONE
                            binding.layoutEditProfilePictureAccount.visibility = View.GONE
                            binding.layoutButtonsAccount.visibility = View.VISIBLE
                            binding.tglbtnFollowAccount.isChecked = state.userAccountDetails.isUserFollowed
                        }

                        companyPostsAdapter.hideLoading()
                        companyPostsAdapter.setProfileImage(state.userAccountDetails.userProfileImage)
                        companyPostsAdapter.submitList(state.userAccountDetails.postsUi)
                    } else {
                        CustomInfoDialog(
                            title = getString(R.string.error),
                            message = state.error.toString(requireContext())
                        ).show(parentFragmentManager, "info_dialog")
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userAccountViewModel.openFollowersPopUpEvent.collect { event ->
                    when(event) {
                        is OpenFollowersFollowingPopUpWindowEvent.Success -> {
                            followersFollowingPopUpWindow.createPopUpWindow(
                                type = "followers",
                                users = event.usersUi,
                                goToUserProfile = { userId, userType ->
                                    bundle.putString(IntentKeys.USER_ID, userId)
                                    bundle.putString(IntentKeys.USER_TYPE, userType.name)

                                    screensNavigator.navigateToMyAccountFragment(bundle)
                                }
                            )
                        }

                        is OpenFollowersFollowingPopUpWindowEvent.ShowError -> {
                            CustomInfoDialog(
                                title = getString(R.string.error),
                                message = event.message.toString(requireContext())
                            ).show(parentFragmentManager, "info_dialog")
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userAccountViewModel.openFollowingPopUpEvent.collect { event ->
                    when(event) {
                        is OpenFollowersFollowingPopUpWindowEvent.Success -> {
                            followersFollowingPopUpWindow.createPopUpWindow(
                                type = "following",
                                users = event.usersUi,
                                goToUserProfile = { userId, userType ->
                                    bundle.putString(IntentKeys.USER_ID, userId)
                                    bundle.putString(IntentKeys.USER_TYPE, userType.name)

                                    screensNavigator.navigateToMyAccountFragment(bundle)
                                }
                            )
                        }

                        is OpenFollowersFollowingPopUpWindowEvent.ShowError -> {
                            CustomInfoDialog(
                                title = getString(R.string.error),
                                message = event.message.toString(requireContext())
                            ).show(parentFragmentManager, "info_dialog")
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userAccountViewModel.submitCommentEvent.collect { event ->
                    when(event) {
                        is SubmitCommentEvent.ClearCommentText -> {

                        }
                        is SubmitCommentEvent.ShowError -> {
                            CustomInfoDialog(
                                title = getString(R.string.error),
                                message = event.message.toString(requireContext())
                            ).show(parentFragmentManager, "info_dialog")
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userAccountViewModel.setProfilePictureEvent.collect { event ->
                    when(event) {
                        is SetProfilePictureEvent.ShowError -> {
                            CustomInfoDialog(
                                title = getString(R.string.error),
                                message = event.message.toString(requireContext())
                            ).show(parentFragmentManager, "info_dialog")
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userAccountViewModel.followUnfollowCompanyEvent.collect { event ->
                    when(event) {
                        is FollowUnfollowCompanyEvent.ShowError -> {
                            CustomInfoDialog(
                                title = getString(R.string.error),
                                message = event.message.toString(requireContext())
                            ).show(parentFragmentManager, "info_dialog")
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                userAccountViewModel.startConversationEvent.collect { event ->
                    when (event) {
                        is StartConversationEvent.Success -> {
                            val conversationId = event.conversationId

                            bundle.putString(IntentKeys.USER_ID, userId)
                            bundle.putString(IntentKeys.CONVERSATION_ID, conversationId)

                            screensNavigator.navigateToChatFragment(bundle)
                        }

                        is StartConversationEvent.ShowError -> {
                            CustomInfoDialog(
                                title = getString(R.string.error),
                                message = event.message.toString(requireContext())
                            ).show(parentFragmentManager, "info_dialog")
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerViewPosts() {
        companyPostsAdapter = CompanyPostsAdapter(
            context = requireContext(),
            goToUserProfile = { userId, userType ->
                bundle.putString(IntentKeys.USER_ID, userId)
                bundle.putString(IntentKeys.USER_TYPE, userType.name)

                screensNavigator.navigateToMyAccountFragment(bundle)
            },
            submitReaction = { postId ->
                userAccountViewModel.submitReaction(postId)
            },
            openReactions = { reactions ->
                reactionsPopUpWindow.createPopUpWindow(
                    reactions = reactions,
                    goToUserProfile = { userId, userType ->
                        bundle.putString(IntentKeys.USER_ID, userId)
                        bundle.putString(IntentKeys.USER_TYPE, userType.name)

                        screensNavigator.navigateToMyAccountFragment(bundle)
                    }
                )
            },
            onToggleComments = { postId ->
                userAccountViewModel.changePostCommentSectionVisibility(postId)
            },
            submitComment = { postId, text ->
                userAccountViewModel.submitComment(postId, text)
            }
        )

        binding.rvPostsAccount.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = companyPostsAdapter
        }
    }

    private fun setupClickListeners() {
        binding.ivBackArrowAccount.setOnClickListener {
            screensNavigator.onBackPressed()
        }

        binding.ivSettingsAccount.setOnClickListener {
            screensNavigator.openSettingsSideSheet()
        }

        binding.layoutUserFollowersAccount.setOnClickListener {
            val followers = userAccountViewModel.userAccountUiState.value.userAccountDetails.followers
            userAccountViewModel.openFollowersPopUp(followers)
        }

        binding.layoutUserFollowingAccount.setOnClickListener {
            val following = userAccountViewModel.userAccountUiState.value.userAccountDetails.following
            userAccountViewModel.openFollowingPopUp(following)
        }

        binding.layoutEditProfilePictureAccount.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.tglbtnFollowAccount.setOnClickListener {
            userAccountViewModel.followUnfollowCompany(userId, binding.tglbtnFollowAccount.isChecked)
        }

        binding.btnMessageAccount.setOnClickListener {
            if (userId != null && userType != null) {
                userAccountViewModel.startConversation(userId!!, userType!!)
            }
        }

        binding.rgTabsAccount.setOnCheckedChangeListener { group, checkedId ->
                when(checkedId) {
                    R.id.rb_reactions_person_account -> {
                        userAccountViewModel.getPostsWherePersonReacted(userId)
                    }

                    R.id.rb_comments_person_account -> {
                        userAccountViewModel.getPostsWherePersonCommented(userId)
                    }
                }
        }

        binding.rvPostsAccount.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                val threshold = 3

                if (lastVisibleItemPosition >= totalItemCount - threshold) {
                    if (!userAccountViewModel.isEndReached()) {
                        companyPostsAdapter.showLoading()

                        if (binding.layoutPersonReactionsCommentsAccount.isGone) {
                            userAccountViewModel.loadMoreCompanyPosts(userId)
                        } else {
                            when {
                                binding.rbReactionsPersonAccount.isChecked -> {
                                    userAccountViewModel.loadMorePostsWherePersonReacted(userId)
                                }

                                binding.rbCommentsPersonAccount.isChecked -> {
                                    userAccountViewModel.loadMorePostsWherePersonCommented(userId)
                                }
                            }
                        }
                    } else {
                        companyPostsAdapter.hideLoading()
                    }
                }

                if (firstVisibleItemPosition == RecyclerView.NO_POSITION || lastVisibleItemPosition == RecyclerView.NO_POSITION) return

                val firstSafeVisiblePosition = maxOf(firstVisibleItemPosition - 5, 0)
                val lastSafeVisiblePosition = minOf(lastVisibleItemPosition + 5, companyPostsAdapter.currentList.lastIndex)

                if (firstSafeVisiblePosition > lastSafeVisiblePosition) return

                val visiblePosts = companyPostsAdapter.currentList.subList(firstSafeVisiblePosition, lastSafeVisiblePosition + 1)
                val visiblePostIds = visiblePosts.map { it.id }

                userAccountViewModel.updateVisiblePostIds(visiblePostIds)
            }
        })
    }
}