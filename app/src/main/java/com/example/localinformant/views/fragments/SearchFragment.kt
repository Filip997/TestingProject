package com.example.localinformant.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.core.presentation.navigator.ScreensNavigator
import com.example.localinformant.databinding.FragmentSearchBinding
import com.example.localinformant.search.presentation.viewmodels.SearchViewModel
import com.example.localinformant.views.adapters.SearchUsersAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by viewModels()

    @Inject lateinit var screensNavigator: ScreensNavigator

    private lateinit var bundle: Bundle
    private lateinit var searchUsersAdapter: SearchUsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        bundle = Bundle()

        setupViewModels()
        setupRecyclerViewSearchedUsers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.svSearchUsers.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchViewModel.searchUsersByName(query ?: "")
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchViewModel.searchUsersByName(newText ?: "")
        return false
    }

    private fun setupViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.searchUiState.collect { state ->

                    if (state.isLoading)
                        binding.progressbarSearch.visibility = View.VISIBLE
                    else
                        binding.progressbarSearch.visibility = View.GONE

                    searchUsersAdapter.submitList(state.searchedUsers)
                }
            }
        }
    }

    private fun setupRecyclerViewSearchedUsers() {
        searchUsersAdapter = SearchUsersAdapter(
            context = requireContext(),
            goToUserProfile = { userId, userType ->
                bundle.putString(IntentKeys.USER_ID, userId)
                bundle.putString(IntentKeys.USER_TYPE, userType.name)

                screensNavigator.navigateToMyAccountFragment(bundle)
            }
        )

        binding.rvSearchUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchUsersAdapter
        }
    }
}