package com.example.localinformant.setup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.setup.domain.usecases.SaveUserTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginChooserViewModel @Inject constructor(
    private val saveUserTypeUseCase: SaveUserTypeUseCase
) : ViewModel() {

    fun saveUserType(userType: UserType) {
        saveUserTypeUseCase.invoke(userType)
    }
}