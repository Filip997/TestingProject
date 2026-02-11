package com.example.localinformant.language.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.core.domain.models.Language
import com.example.localinformant.core.presentation.AppLanguageManager
import com.example.localinformant.language.domain.GetAppLanguageUseCase
import com.example.localinformant.language.domain.SaveAppLanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val saveAppLanguageUseCase: SaveAppLanguageUseCase,
    private val getAppLanguageUseCase: GetAppLanguageUseCase,
    private val appLanguageManager: AppLanguageManager
) : ViewModel() {

    fun onLanguageSelected(language: Language) {
        viewModelScope.launch {
            val languageCode = language.code
            appLanguageManager.setAppLanguage(languageCode)
            saveAppLanguageUseCase.invoke(languageCode)
        }
    }

    fun getAppLanguageCode(): String? {
        return getAppLanguageUseCase.invoke()
    }
}