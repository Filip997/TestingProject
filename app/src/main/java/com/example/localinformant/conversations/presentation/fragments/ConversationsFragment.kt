package com.example.localinformant.conversations.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localinformant.conversations.presentation.adapters.ConversationsAdapter
import com.example.localinformant.conversations.presentation.viewmodels.ConversationsViewModel
import com.example.localinformant.core.presentation.constants.IntentKeys
import com.example.localinformant.core.presentation.navigator.ScreensNavigator
import com.example.localinformant.core.presentation.util.MyNotificationManager
import com.example.localinformant.databinding.FragmentConversationsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ConversationsFragment : Fragment() {

    private var _binding: FragmentConversationsBinding? = null
    private val binding get() = _binding!!

    private val conversationsViewModel: ConversationsViewModel by viewModels()

    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var myNotificationManager: MyNotificationManager

    private lateinit var bundle: Bundle
    private lateinit var conversationsAdapter: ConversationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConversationsBinding.inflate(inflater, container, false)

        bundle = Bundle()

        setupViewModels()
        setupRecyclerView()
        setupClickListeners()

        return binding.root
    }

    private fun setupViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                conversationsViewModel.conversationsUiState.collect { uiState ->

                    if (uiState.isLoading)
                        binding.progressbarConversations.visibility = View.VISIBLE
                    else
                        binding.progressbarConversations.visibility = View.GONE

                    conversationsAdapter.submitList(uiState.conversations)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                myNotificationManager.unreadMessagesCount.collect {
                    conversationsAdapter.setUnreadMessagesUserIds(it.toList())
                }
            }
        }
    }

    private fun setupRecyclerView() {
        conversationsAdapter = ConversationsAdapter(
            context = requireContext(),
            onConversationClick = { participant2Id, conversationId ->
                bundle.putString(IntentKeys.USER_ID, participant2Id)
                bundle.putString(IntentKeys.CONVERSATION_ID, conversationId)

                myNotificationManager.decrementMessagesCount(participant2Id)
                screensNavigator.navigateToChatFragment(bundle)
            }
        )
        binding.rvConversations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = conversationsAdapter
        }
    }

    private fun setupClickListeners() {
        binding.ivConversationsBackArrow.setOnClickListener {
            screensNavigator.onBackPressed()
        }

        binding.fabNewConversation.setOnClickListener {
            screensNavigator.openNewConversationDialogFragment()
        }
    }
}