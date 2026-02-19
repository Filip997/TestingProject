package com.example.localinformant.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.auth.domain.usecases.ResetPasswordUseCase
import com.example.localinformant.auth.domain.error.AuthError
import com.example.localinformant.auth.domain.validators.ForgotPasswordValidator
import com.example.localinformant.auth.presentation.events.ForgotPasswordEvent
import com.example.localinformant.auth.presentation.models.ForgotPasswordUiState
import com.example.localinformant.core.domain.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordValidator: ForgotPasswordValidator,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _forgotPasswordUiState: MutableStateFlow<ForgotPasswordUiState> = MutableStateFlow(
        ForgotPasswordUiState())
    val forgotPasswordUiState = _forgotPasswordUiState.asStateFlow()

    private val _forgotPasswordEvent: MutableSharedFlow<ForgotPasswordEvent> = MutableSharedFlow()
    val forgotPasswordEvent = _forgotPasswordEvent.asSharedFlow()

    fun onEmailChange(email: String) {
        viewModelScope.launch {
            val emailError = forgotPasswordValidator.validateEmail(email)
            _forgotPasswordUiState.update {
                val isSendEmailEnabled = emailError == null

                it.copy(
                    email = email,
                    emailError = emailError,
                    isSendEmailEnabled = isSendEmailEnabled
                )
            }
        }
    }

    fun resetPassword() {
        viewModelScope.launch(Dispatchers.IO) {
            _forgotPasswordUiState.update {
                it.copy(
                    isLoading = true
                )
            }

            val uiState = _forgotPasswordUiState.value

            when(val result = resetPasswordUseCase.invoke(uiState.email!!)) {
                is Result.Success -> _forgotPasswordEvent.emit(ForgotPasswordEvent.ShowSuccessDialog)
                is Result.Error -> _forgotPasswordEvent.emit(ForgotPasswordEvent.ShowError(result.error as AuthError))
            }

            _forgotPasswordUiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }
}