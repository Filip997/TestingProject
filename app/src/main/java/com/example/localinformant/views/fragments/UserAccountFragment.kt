package com.example.localinformant.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.databinding.FragmentUserAccountBinding
import com.example.localinformant.viewmodels.CompanyViewModel
import com.example.localinformant.viewmodels.PersonViewModel

class UserAccountFragment : Fragment() {

    private lateinit var binding: FragmentUserAccountBinding

    private lateinit var personViewModel: PersonViewModel
    private lateinit var companyViewModel: CompanyViewModel

    private var userType: String? = null
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserAccountBinding.inflate(inflater)

        if (arguments?.containsKey(IntentKeys.USER_TYPE)!!) {
            userType = arguments?.getString(IntentKeys.USER_TYPE)
        }

        if (arguments?.containsKey(IntentKeys.USER_ID)!!) {
            userId = arguments?.getString(IntentKeys.USER_ID)
        }

        personViewModel = ViewModelProvider(this)[PersonViewModel::class.java]
        companyViewModel = ViewModelProvider(this)[CompanyViewModel::class.java]

        setupViewModels()
        setupUI()

        return binding.root
    }

    private fun setupUI() {
        if (userType == AppConstants.PERSON) {
            binding.layoutButtons.visibility = View.GONE
            binding.layoutPosts.visibility = View.GONE

            personViewModel.getPersonById(userId ?: "")
        } else if (userType == AppConstants.COMPANY) {
            binding.layoutButtons.visibility = View.VISIBLE
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
    }
}