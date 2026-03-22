package com.example.localinformant.home.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localinformant.R
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.core.presentation.dialogs.CustomInfoDialog
import com.example.localinformant.core.presentation.navigator.ScreensNavigator
import com.example.localinformant.core.presentation.util.toString
import com.example.localinformant.databinding.FragmentHomeBinding
import com.example.localinformant.home.presentation.events.SubmitCommentEvent
import com.example.localinformant.home.presentation.util.ReactionsPopUpWindow
import com.example.localinformant.home.presentation.viewmodels.HomeViewModel
import com.example.localinformant.core.presentation.adapters.CompanyPostsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()

    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var reactionsPopUpWindow: ReactionsPopUpWindow

    private lateinit var bundle: Bundle
    private lateinit var companyPostsAdapter: CompanyPostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)

        bundle = Bundle()

        setupViewModels()
        setupRecyclerViewPosts()
        setOnSwipeRefresh()

        return binding.root
    }

    private fun setupViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.homeUiState.collect { state ->
                    binding.swipeRefreshLayoutHome.isRefreshing = false

                    if (state.isLoading)
                        binding.progressbarHome.visibility = View.VISIBLE
                    else
                        binding.progressbarHome.visibility = View.GONE

                    companyPostsAdapter.hideLoading()
                    companyPostsAdapter.submitList(state.postsUi)

                    if (state.error != null) {
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
                homeViewModel.submitCommentEvent.collect { event ->
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
                homeViewModel.submitReaction(postId)
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
                homeViewModel.changePostCommentSectionVisibility(postId)
            },
            submitComment = { postId, text ->
                homeViewModel.submitComment(postId, text)
            }
        )

        binding.rvHomePosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = companyPostsAdapter
        }

        binding.rvHomePosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy <= 0) return

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                val threshold = 3

                if (lastVisibleItemPosition >= totalItemCount - threshold) {
                    if (!homeViewModel.isEndReached()) {
                        companyPostsAdapter.showLoading()
                        homeViewModel.loadMorePosts()
                    } else {
                        companyPostsAdapter.hideLoading()
                    }
                }
            }
        })
    }

    private fun setOnSwipeRefresh() {
        binding.swipeRefreshLayoutHome.setOnRefreshListener {
            homeViewModel.getPosts()
        }
    }
}