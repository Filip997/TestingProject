package com.example.localinformant.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.localinformant.databinding.FragmentUsersListBinding

class UsersListFragment : Fragment() {

    private lateinit var binding: FragmentUsersListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersListBinding.inflate(inflater)
        return binding.root
    }
}