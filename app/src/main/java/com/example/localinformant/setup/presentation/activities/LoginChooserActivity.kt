package com.example.localinformant.setup.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.viewbinding.ViewBinding
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.presentation.navigator.ScreensNavigator
import com.example.localinformant.databinding.ActivityLoginChooserBinding
import com.example.localinformant.setup.presentation.viewmodels.LoginChooserViewModel
import com.example.localinformant.core.presentation.activities.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginChooserActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginChooserBinding

    private val loginChooserViewModel: LoginChooserViewModel by viewModels()

    @Inject
    lateinit var screensNavigator: ScreensNavigator

    override fun getLayoutBinding(): ViewBinding {
        binding = ActivityLoginChooserBinding.inflate(layoutInflater)
        return binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.cardLoginChooserPerson.setOnClickListener {
            loginChooserViewModel.saveUserType(UserType.PERSON)
            screensNavigator.navigateToLoginActivity()
        }

        binding.cardLoginChooserCompany.setOnClickListener {
            loginChooserViewModel.saveUserType(UserType.COMPANY)
            screensNavigator.navigateToLoginActivity()
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginChooserActivity::class.java)
            context.startActivity(intent)
        }
    }
}