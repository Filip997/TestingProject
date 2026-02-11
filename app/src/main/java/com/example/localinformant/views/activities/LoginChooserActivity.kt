package com.example.localinformant.views.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.core.presentation.ScreensNavigator
import com.example.localinformant.databinding.ActivityLoginChooserBinding
import javax.inject.Inject

class LoginChooserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginChooserBinding

    @Inject lateinit var screensNavigator: ScreensNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginChooserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardPerson.setOnClickListener {
            screensNavigator.navigateToLoginActivity(AppConstants.PERSON)
        }

        binding.cardCompany.setOnClickListener {
            screensNavigator.navigateToLoginActivity(AppConstants.COMPANY)
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginChooserActivity::class.java)
            context.startActivity(intent)
        }
    }
}