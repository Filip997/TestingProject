package com.example.localinformant.conversations.presentation.fragments

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
import com.example.localinformant.conversations.presentation.adapters.ChatAdapter
import com.example.localinformant.conversations.presentation.events.GetChatParticipant2UserTypeEvent
import com.example.localinformant.conversations.presentation.events.SendMessageEvent
import com.example.localinformant.conversations.presentation.viewmodels.ConversationsViewModel
import com.example.localinformant.core.domain.models.UserStatus
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.presentation.constants.IntentKeys
import com.example.localinformant.core.presentation.dialogs.CustomInfoDialog
import com.example.localinformant.core.presentation.navigator.ScreensNavigator
import com.example.localinformant.core.presentation.util.MyNotificationManager
import com.example.localinformant.core.presentation.util.loadImage
import com.example.localinformant.core.presentation.util.toString
import com.example.localinformant.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val conversationViewModel: ConversationsViewModel by viewModels()

    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var myNotificationManager: MyNotificationManager

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var bundle: Bundle
    private var participant2Id: String? = null
    private var participant2UserType: UserType? = null
    private var conversationId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        bundle = Bundle()

        if (arguments != null) {
            participant2Id = arguments?.getString(IntentKeys.USER_ID) ?: ""
            conversationId = arguments?.getString(IntentKeys.CONVERSATION_ID) ?: ""

            if (participant2Id != null && conversationId != null) {
                conversationViewModel.loadConversationChat(participant2Id!!, conversationId!!)
            }
        }

        setupViewModels()
        setupRecyclerView()
        setupClickListeners()

        return binding.root
    }

    private fun setupViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                conversationViewModel.chatUiState.collect { uiState ->

                    if (uiState.isLoading)
                        binding.progressbarChat.visibility = View.VISIBLE
                    else
                        binding.progressbarChat.visibility = View.GONE

                    if (uiState.isLoadingGoingToUserProfile)
                        binding.progressbarTopBarChat.visibility = View.VISIBLE
                    else
                        binding.progressbarTopBarChat.visibility = View.GONE

                    if (uiState.isLoadingMore) {
                        chatAdapter.showLoading()
                    } else {
                        chatAdapter.hideLoading()
                    }

                    loadImage(
                        requireContext(),
                        uiState.participant2ProfileImage,
                        binding.civChatOtherUserProfileImage
                    )
                    binding.tvChatOtherUserName.text = uiState.participant2Name

                    if (uiState.participant2Status == UserStatus.ONLINE) {
                        binding.layoutUserStatusActive.visibility = View.VISIBLE
                    } else {
                        binding.layoutUserStatusActive.visibility = View.GONE
                    }

                    chatAdapter.setParticipant2UserId(uiState.participant2Id)
                    chatAdapter.setParticipant2ProfileImage(uiState.participant2ProfileImage)

                    myNotificationManager.decrementMessagesCount(uiState.participant2Id)
                    chatAdapter.submitList(uiState.messages.distinct())
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                conversationViewModel.getChatParticipant2UserTypeEvent.collect { event ->
                    when(event) {
                        is GetChatParticipant2UserTypeEvent.Success -> {
                            participant2UserType = event.userType

                            bundle.putString(IntentKeys.USER_ID, participant2Id)
                            bundle.putString(IntentKeys.USER_TYPE, participant2UserType?.name)

                            screensNavigator.navigateToMyAccountFragment(bundle)
                        }
                        is GetChatParticipant2UserTypeEvent.ShowError -> {
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
                conversationViewModel.sendMessageEvent.collect { event ->
                    when (event) {
                        is SendMessageEvent.Success -> {

                        }

                        is SendMessageEvent.ShowError -> {
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

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(requireContext())

        binding.rvChat.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                RecyclerView.VERTICAL,
                true
            )
        }

        binding.rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (firstVisibleItemPosition == RecyclerView.NO_POSITION || lastVisibleItemPosition == RecyclerView.NO_POSITION) return

                val threshold = 3

                if (lastVisibleItemPosition >= totalItemCount - threshold) {
                    if (!conversationViewModel.isEndReached()) {
                        chatAdapter.showLoading()
                        conversationViewModel.loadMoreMessagesForConversation(conversationId ?: "")
                    } else {
                        chatAdapter.hideLoading()
                    }
                }
            }
        })
    }

    private fun setupClickListeners() {
        binding.ivChatBackArrow.setOnClickListener {
            screensNavigator.onBackPressed()
        }

        binding.civChatOtherUserProfileImage.setOnClickListener {
            if (participant2Id != null) {
                if (participant2UserType == null) {
                    conversationViewModel.goToUserProfile(participant2Id!!)
                } else {
                    bundle.putString(IntentKeys.USER_ID, participant2Id)
                    bundle.putString(IntentKeys.USER_TYPE, participant2UserType?.name)

                    screensNavigator.navigateToMyAccountFragment(bundle)
                }
            }
        }

        binding.tvChatOtherUserName.setOnClickListener {
            if (participant2Id != null) {
                if (participant2UserType == null) {
                    conversationViewModel.goToUserProfile(participant2Id!!)
                } else {
                    bundle.putString(IntentKeys.USER_ID, participant2Id)
                    bundle.putString(IntentKeys.USER_TYPE, participant2UserType?.name)

                    screensNavigator.navigateToMyAccountFragment(bundle)
                }
            }
        }

        binding.ivChatSendBtn.setOnClickListener {
            val messageText = binding.tilChatUserNewMessage.editText?.text.toString().trim()

            if (messageText.isNotEmpty()) {
                conversationViewModel.sendMessageToConversation(
                    conversationId ?: "",
                    participant2Id ?: "",
                    messageText
                )

                binding.tilChatUserNewMessage.editText?.text?.clear()
            }
        }
    }
}