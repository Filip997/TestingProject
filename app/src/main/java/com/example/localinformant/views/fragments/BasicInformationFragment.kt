package com.example.localinformant.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.localinformant.databinding.FragmentBasicInformationBinding

class BasicInformationFragment : Fragment() {

    private lateinit var binding: FragmentBasicInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBasicInformationBinding.inflate(inflater)
        return binding.root
    }
}