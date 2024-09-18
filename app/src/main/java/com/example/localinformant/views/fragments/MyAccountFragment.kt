package com.example.localinformant.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.localinformant.R
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.databinding.FragmentMyAccountBinding
import com.example.localinformant.viewmodels.MyAccountViewModel
import com.example.localinformant.views.activities.ChangePasswordActivity
import com.example.localinformant.views.activities.LoginActivity
import com.example.localinformant.views.activities.LoginSettingsActivity
import com.example.localinformant.views.activities.MainActivity

class MyAccountFragment : Fragment() {

    private var _binding: FragmentMyAccountBinding? = null
    private val binding get() = _binding!!
    private val accountViewModel: MyAccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtonClicks()
        setupViewModels()
        binding.accountEmailTv.text = accountViewModel.user?.email.toString()
    }


    private fun setupButtonClicks() {


        binding.accountLoginSettingsLayout.setOnClickListener {
            startActivity(Intent(context, LoginSettingsActivity::class.java))
        }

        binding.accountLogoutLayout.setOnClickListener {
            accountViewModel.logout()
        }
    }


    private fun setupViewModels(){
        accountViewModel.signedOut.observe(viewLifecycleOwner) { signedOut ->
            if (signedOut) {
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }
    }
}