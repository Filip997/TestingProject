package com.example.localinformant.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localinformant.R
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.constants.NavFunctions.popUpDefaultNavigation
import com.example.localinformant.databinding.FragmentUserAccountBinding
import com.example.localinformant.viewmodels.CompanyViewModel
import com.example.localinformant.viewmodels.PersonViewModel
import com.example.localinformant.viewmodels.PostViewModel
import com.example.localinformant.views.adapters.CompanyPostsAdapter

class UserAccountFragment : Fragment() {

    private lateinit var binding: FragmentUserAccountBinding
    private lateinit var navController: NavController

    private lateinit var personViewModel: PersonViewModel
    private lateinit var companyViewModel: CompanyViewModel
    private lateinit var postViewModel: PostViewModel

    private var userType: String? = null
    private var accountUserType: String? = null
    private var userId: String? = null
    private lateinit var companyPostsAdapter: CompanyPostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserAccountBinding.inflate(inflater)

        if (arguments?.containsKey(IntentKeys.USER_TYPE)!!) {
            userType = arguments?.getString(IntentKeys.USER_TYPE)
        }

        if (arguments?.containsKey(IntentKeys.ACCOUNT_USER_TYPE)!!) {
            accountUserType = arguments?.getString(IntentKeys.ACCOUNT_USER_TYPE)
        }

        if (arguments?.containsKey(IntentKeys.USER_ID)!!) {
            userId = arguments?.getString(IntentKeys.USER_ID)
        }

        personViewModel = ViewModelProvider(this)[PersonViewModel::class.java]
        companyViewModel = ViewModelProvider(this)[CompanyViewModel::class.java]
        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        companyPostsAdapter = CompanyPostsAdapter(mutableListOf()) { companyId ->
            goToCompanyProfile(
                companyId
            )
        }
        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPosts.adapter = companyPostsAdapter

        if (accountUserType == AppConstants.COMPANY) {
            companyViewModel.isCompanyFollowed(userId!!)
            postViewModel.getPostsByCompanyId(userId!!)
        }

        setupViewModels()
        setupUI()
        setupClickListeners()
        setOnSwipeRefresh()

        return binding.root
    }

    private fun setupUI() {
        if (accountUserType == AppConstants.PERSON) {
            binding.layoutButtons.visibility = View.GONE
            binding.layoutPosts.visibility = View.GONE

            personViewModel.getPersonById(userId ?: "")
        } else if (accountUserType == AppConstants.COMPANY) {
            binding.layoutButtons.visibility = if (userType == AppConstants.PERSON) View.VISIBLE else View.GONE
            binding.layoutPosts.visibility = View.VISIBLE

            companyViewModel.getCompanyById(userId ?: "")
        }
    }

    private fun setupViewModels() {
        personViewModel.personLiveData.observe(viewLifecycleOwner) { person ->
            if (person != null) {
                binding.tvFullName.text = person.fullName
                binding.tvFollowersFollowingText.text = "following"
                binding.tvFollowersFollowing.text = person.following.toString()
            }
        }

        companyViewModel.companyLiveData.observe(viewLifecycleOwner) { company ->
            if (company != null) {
                binding.tvFullName.text = company.companyName
                binding.tvFollowersFollowingText.text = "followers"
                binding.tvFollowersFollowing.text = company.followers.toString()
            }
        }

        companyViewModel.isCompanyFollowed.observe(viewLifecycleOwner) {
            binding.tbtnFollowFollowing.isChecked = it
        }

        companyViewModel.isSuccessful.observe(viewLifecycleOwner) {
            val isChecked = binding.tbtnFollowFollowing.isChecked
            if (!it) {
                binding.tbtnFollowFollowing.isChecked = !isChecked
            }
        }

        postViewModel.postsByCompanyId.observe(viewLifecycleOwner) { posts ->
            if (!posts.isNullOrEmpty()) {
                companyPostsAdapter.updateList(posts)
            }
        }
    }

    private fun setOnSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (accountUserType == AppConstants.COMPANY) {
                postViewModel.getPostsByCompanyId(userId!!)
            }
        }
    }

    private fun setupClickListeners() {
        if (userType == AppConstants.PERSON) {
            binding.tbtnFollowFollowing.setOnClickListener {
                companyViewModel.followUnfollowCompany(userId!!)
            }
        }
    }

    private fun goToCompanyProfile(companyId: String) {
        val bundle = Bundle()
        bundle.putString(IntentKeys.USER_ID, companyId)
        bundle.putString(IntentKeys.USER_TYPE, this.userType)
        bundle.putString(IntentKeys.ACCOUNT_USER_TYPE, AppConstants.COMPANY)

        navController.navigate(
            R.id.userAccountFragment, bundle,
            popUpDefaultNavigation()
        )
    }
}