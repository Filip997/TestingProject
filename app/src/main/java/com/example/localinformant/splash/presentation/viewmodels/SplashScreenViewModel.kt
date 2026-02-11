package com.example.localinformant.splash.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.core.presentation.AppLanguageManager
import com.example.localinformant.splash.domain.GetLanguageCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val getLanguageCodeUseCase: GetLanguageCodeUseCase,
    private val appLanguageManager: AppLanguageManager
) : ViewModel() {

    fun setAppLanguageOnStart() {
        viewModelScope.launch {
            val currentLanguageCode = getLanguageCodeUseCase.invoke()
            if (!currentLanguageCode.isNullOrEmpty()) {
                appLanguageManager.setAppLanguage(currentLanguageCode)
            }
        }
    }
}