package com.example.localinformant.language.presentation.activities

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.viewbinding.ViewBinding
import com.example.localinformant.core.presentation.getLanguages
import com.example.localinformant.core.domain.models.Language
import com.example.localinformant.databinding.ActivityLanguageBinding
import com.example.localinformant.language.presentation.viewmodels.LanguageViewModel
import com.example.localinformant.views.activities.BaseActivity
import com.example.localinformant.views.activities.LoginChooserActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageActivity : BaseActivity() {

    private lateinit var binding: ActivityLanguageBinding

    private val languageViewModel: LanguageViewModel by viewModels()

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

        binding.btnNext.setOnClickListener {
            val intent = Intent(this@LanguageActivity, LoginChooserActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setInitialSelectedItemOnSpinner(languages: List<Language>) {
        val currentLanguageCode = languageViewModel.getAppLanguageCode()
        if (!currentLanguageCode.isNullOrEmpty()) {
            val index = languages.map { it.code }.indexOf(currentLanguageCode)
            binding.spinnerLanguage.setSelection(index)
        }
    }
}