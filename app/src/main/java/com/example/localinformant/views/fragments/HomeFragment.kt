package com.example.localinformant.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localinformant.application.AppController
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.constants.PreferencesManager
import com.example.localinformant.constants.SharedPrefKeys
import com.example.localinformant.databinding.FragmentHomeBinding
import com.example.localinformant.models.Post
import com.example.localinformant.viewmodels.PostViewModel
import com.example.localinformant.views.adapters.CompanyPostsAdapter
import kotlin.concurrent.thread

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var postViewModel: PostViewModel

    private var userType: String? = null
    private lateinit var companyPostsAdapter: CompanyPostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)

        if (arguments?.containsKey(IntentKeys.USER_TYPE) != null && arguments?.containsKey(IntentKeys.USER_TYPE)!!) {
            userType = arguments?.getString(IntentKeys.USER_TYPE)
        } else {
            userType = PreferencesManager.getUserType()
        }

        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        setupViewModels()

        companyPostsAdapter = CompanyPostsAdapter(mutableListOf())
        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPosts.adapter = companyPostsAdapter

        if (userType == AppConstants.PERSON) {
            postViewModel.getAllPostsByFollowedCompaniesFromCurrentPerson()
        } else if (userType == AppConstants.COMPANY) {
            postViewModel.getCurrentCompanyPosts()
        }

        setOnSwipeRefresh()

        return binding.root
    }

    private fun setupViewModels() {
        postViewModel.currentCompanyPostsLiveData.observe(viewLifecycleOwner) { posts ->
            if (!posts.isNullOrEmpty()) {
                companyPostsAdapter.updateList(posts)
            }
        }
    }

    private fun setOnSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (userType == AppConstants.PERSON) {
                postViewModel.getAllPostsByFollowedCompaniesFromCurrentPerson()
            } else if (userType == AppConstants.COMPANY) {
                postViewModel.getCurrentCompanyPosts()
            }
        }
    }
}