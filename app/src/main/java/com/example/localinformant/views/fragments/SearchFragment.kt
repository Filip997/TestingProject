package com.example.localinformant.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localinformant.R
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.constants.NavFunctions.isFragmentInBackStack
import com.example.localinformant.constants.NavFunctions.popUpDefaultNavigation
import com.example.localinformant.databinding.FragmentMyAccountBinding
import com.example.localinformant.databinding.FragmentSearchBinding
import com.example.localinformant.models.User
import com.example.localinformant.viewmodels.UserViewModel
import com.example.localinformant.views.adapters.SearchUsersAdapter

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    private lateinit var userViewModel: UserViewModel

    private lateinit var searchUsersAdapter: SearchUsersAdapter
    private var searchListUsers = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        navController = findNavController()

        searchUsersAdapter = SearchUsersAdapter(
            requireContext(),
            searchListUsers,
            { userId, userType -> onUserClicked(userId, userType) }
            )
        binding.rvSearchUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchUsers.adapter = searchUsersAdapter

        setupViewModels()
        userViewModel.searchUsersByName()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchBarEt.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchUsersAdapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchUsersAdapter.filter.filter(newText)
        return false
    }

    private fun setupViewModels() {
        userViewModel.usersLiveData.observe(viewLifecycleOwner) { usersList ->
            Log.d("searchUser", usersList.toString() ?: "zzz")

            if (usersList != null) {
                searchUsersAdapter.updateList(usersList)
            }
        }
    }

    private fun onUserClicked(userId: String, userType: String) {
        val bundle = Bundle()
        bundle.putString(IntentKeys.USER_ID, userId)
        bundle.putString(IntentKeys.USER_TYPE, userType)

        navController.navigate(
            R.id.userAccountFragment, bundle,
            popUpDefaultNavigation()
        )
    }
}