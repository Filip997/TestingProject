package com.example.localinformant.setup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.core.domain.models.Language
import com.example.localinformant.core.presentation.language.AppLanguageManager
import com.example.localinformant.setup.domain.usecases.GetAppLanguageUseCase
import com.example.localinformant.setup.domain.usecases.SaveAppLanguageUseCase
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