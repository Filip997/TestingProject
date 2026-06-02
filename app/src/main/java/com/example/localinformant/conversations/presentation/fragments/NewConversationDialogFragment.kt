package com.example.localinformant.conversations.presentation.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localinformant.R
import com.example.localinformant.conversations.presentation.events.CreateNewConversationEvent
import com.example.localinformant.conversations.presentation.viewmodels.ConversationsViewModel
import com.example.localinformant.core.presentation.constants.IntentKeys
import com.example.localinformant.core.presentation.dialogs.CustomInfoDialog
import com.example.localinformant.core.presentation.navigator.ScreensNavigator
import com.example.localinformant.core.presentation.util.toString
import com.example.localinformant.databinding.FragmentDialogNewConversationBinding
import com.example.localinformant.core.presentation.adapters.SearchUsersAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NewConversationDialogFragment : DialogFragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentDialogNewConversationBinding? = null
    private val binding get() = _binding!!

    private val conversationsViewModel: ConversationsViewModel by viewModels()

    @Inject lateinit var screensNavigator: ScreensNavigator

    private lateinit var bundle: Bundle
    private lateinit var searchUsersAdapter: SearchUsersAdapter

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                (resources.displayMetrics.widthPixels * 0.9).toInt()
            )

            setGravity(Gravity.CENTER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogNewConversationBinding.inflate(inflater, container, false)

        bundle = Bundle()

        setupViewModels()
        setupRecyclerViewSearchedUsers()
        setupClickListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.svNewConversationSearchUsers.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        conversationsViewModel.searchUsersByName(query ?: "")
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        conversationsViewModel.searchUsersByName(newText ?: "")
        return false
    }

    private fun setupViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                conversationsViewModel.newConversationUiState.collect {
                    if (it.isLoading)
                        binding.progressbarNewConversation.visibility = View.VISIBLE
                    else
                        binding.progressbarNewConversation.visibility = View.GONE

                    searchUsersAdapter.submitList(it.searchedUsers)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                conversationsViewModel.createNewConversationEvent.collect { event ->
                    when(event) {
                        is CreateNewConversationEvent.Success -> {
                            val userId = event.userId
                            val conversationId = event.conversationId

                            bundle.putString(IntentKeys.USER_ID, userId)
                            bundle.putString(IntentKeys.CONVERSATION_ID, conversationId)

                            screensNavigator.navigateToChatFragment(bundle)

                            dismiss()
                        }

                        is CreateNewConversationEvent.ShowError -> {
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

    private fun setupRecyclerViewSearchedUsers() {
        searchUsersAdapter = SearchUsersAdapter(
            context = requireContext(),
            onUserClick = { userId, userType ->
                conversationsViewModel.createNewConversationWithUser(userId, userType)
            }
        )

        binding.rvNewConversationUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchUsersAdapter
        }
    }

    private fun setupClickListeners() {
        binding.ivNewConversationClose.setOnClickListener {
            dialog?.dismiss()
        }
    }
}