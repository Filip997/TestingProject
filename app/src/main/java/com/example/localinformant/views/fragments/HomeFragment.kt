package com.example.localinformant.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private var userType: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root

        val args: Bundle? = arguments
        if (args != null) {
            userType = args.getString(IntentKeys.USER_TYPE)
        }
    }
}