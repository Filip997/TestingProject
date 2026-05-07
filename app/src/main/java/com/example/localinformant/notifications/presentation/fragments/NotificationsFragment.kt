package com.example.localinformant.notifications.presentation.fragments

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
import com.example.localinformant.core.presentation.constants.IntentKeys
import com.example.localinformant.core.presentation.navigator.ScreensNavigator
import com.example.localinformant.core.presentation.util.MyNotificationManager
import com.example.localinformant.databinding.FragmentNotificationsBinding
import com.example.localinformant.notifications.presentation.adapters.UserNotificationsAdapter
import com.example.localinformant.notifications.presentation.viewmodels.NotificationsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val notificationsViewModel: NotificationsViewModel by viewModels()

    private lateinit var bundle: Bundle
    private lateinit var notificationsAdapter: UserNotificationsAdapter

    @Inject
    lateinit var screensNavigator: ScreensNavigator
    @Inject
    lateinit var myNotificationManager: MyNotificationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        bundle = Bundle()

        setupViewModels()
        setupRecyclerViewNotifications()
        setOnSwipeRefresh()

        return binding.root
    }

    private fun setupViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                notificationsViewModel.uiState.collect { state ->
                    binding.swipeRefreshLayoutNotifications.isRefreshing = false

                    if (state.isLoading)
                        binding.progressbarNotifications.visibility = View.VISIBLE
                    else
                        binding.progressbarNotifications.visibility = View.GONE

                    notificationsAdapter.submitList(state.notifications)
                }
            }
        }
    }

    private fun setupRecyclerViewNotifications() {
        notificationsAdapter = UserNotificationsAdapter(
            context = requireContext(),
            goToUserProfile = { userId, userType ->
                bundle.putString(IntentKeys.USER_ID, userId)
                bundle.putString(IntentKeys.USER_TYPE, userType.name)

                screensNavigator.navigateToMyAccountFragment(bundle)
            },
            goToSpecificPost = { postId ->
                bundle.putString(IntentKeys.POST_ID, postId)

                screensNavigator.navigateToHomeFragment(bundle)
            }
        )

        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationsAdapter
        }
    }

    private fun setOnSwipeRefresh() {
        binding.swipeRefreshLayoutNotifications.setOnRefreshListener {
            notificationsViewModel.getUserNotifications()
        }
    }

    override fun onResume() {
        super.onResume()

        myNotificationManager.resetNotificationsCount()
    }
}