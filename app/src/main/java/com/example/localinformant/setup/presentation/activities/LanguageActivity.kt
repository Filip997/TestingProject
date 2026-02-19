package com.example.localinformant.setup.presentation.activities

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.viewbinding.ViewBinding
import com.example.localinformant.core.presentation.language.getLanguages
import com.example.localinformant.core.domain.models.Language
import com.example.localinformant.core.presentation.navigator.ScreensNavigator
import com.example.localinformant.databinding.ActivityLanguageBinding
import com.example.localinformant.setup.presentation.viewmodels.LanguageViewModel
import com.example.localinformant.core.presentation.activities.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LanguageActivity : BaseActivity() {

    private lateinit var binding: ActivityLanguageBinding

    private val languageViewModel: LanguageViewModel by viewModels()

    @Inject lateinit var screensNavigator: ScreensNavigator

    override fun getLayoutBinding(): ViewBinding {
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        return binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val languages = getLanguages(this)

        val languageArrayAdapter = ArrayAdapter<String>(
            this,
            R.layout.simple_spinner_item,
            languages.map { it.name }
        )

        languageArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = languageArrayAdapter

        setInitialSelectedItemOnSpinner(languages)

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val language = languages[position]

                languageViewModel.onLanguageSelected(language)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }

        binding.btnLanguageNext.setOnClickListener {
            screensNavigator.navigateToLoginChooserActivity()
        }
    }

    private fun setInitialSelectedItemOnSpinner(languages: List<Language>) {
        val currentLanguageCode = languageViewModel.getAppLanguageCode()
        if (!currentLanguageCode.isNullOrEmpty()) {
            val index = languages.map { it.code }.indexOf(currentLanguageCode)
            binding.spinnerLanguage.setSelection(index)
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LanguageActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish()
        }
    }
}