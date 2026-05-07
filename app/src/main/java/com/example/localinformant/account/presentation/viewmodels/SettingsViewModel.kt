package com.example.localinformant.account.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.account.domain.usecases.ChangeUserPasswordUseCase
import com.example.localinformant.account.domain.usecases.LogoutUserUseCase
import com.example.localinformant.account.domain.validators.ChangePasswordValidator
import com.example.localinformant.account.presentation.events.ChangePasswordEvent
import com.example.localinformant.account.presentation.events.LogoutUserEvent
import com.example.localinformant.account.presentation.models.SettingsUiState
import com.example.localinformant.core.domain.result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val changePasswordValidator: ChangePasswordValidator,
    private val changeUserPasswordUseCase: ChangeUserPasswordUseCase,
    private val logoutUserUseCase: LogoutUserUseCase
) : ViewModel() {

    private val _settingsUiState: MutableStateFlow<SettingsUiState> = MutableStateFlow(SettingsUiState())
    val settingsUiState = _settingsUiState
        .onStart { resetSettingsState() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            SettingsUiState()
        )

    private val _changePasswordEvent: MutableSharedFlow<ChangePasswordEvent> = MutableSharedFlow()
    val changePasswordEvent = _changePasswordEvent.asSharedFlow()

    private val _logoutEvent: MutableSharedFlow<LogoutUserEvent> = MutableSharedFlow()
    val logoutEvent = _logoutEvent.asSharedFlow()

    fun resetSettingsState() {
        _settingsUiState.value = SettingsUiState()
    }

    fun onOldPasswordChange(oldPassword: String) {
        viewModelScope.launch {
            val oldPasswordError = changePasswordValidator.validateOldPassword(oldPassword)
            val newPasswordError = _settingsUiState.value.newPasswordError
            val isChangePasswordEnabled = oldPasswordError == null && newPasswordError == null

            _settingsUiState.update {
                it.copy(
                    oldPassword = oldPassword,
                    oldPasswordError = oldPasswordError,
                    newPasswordError = newPasswordError,
                    isChangePasswordEnabled = isChangePasswordEnabled
                )
            }
        }
    }

    fun onNewPasswordChange(newPassword: String) {
        viewModelScope.launch {
            val newPasswordError = changePasswordValidator.validateNewPassword(newPassword)
            val oldPasswordError = _settingsUiState.value.oldPasswordError
            val isChangePasswordEnabled = newPasswordError == null && oldPasswordError == null

            _settingsUiState.update {
                it.copy(
                    newPassword = newPassword,
                    newPasswordError = newPasswordError,
                    oldPasswordError = oldPasswordError,
                    isChangePasswordEnabled = isChangePasswordEnabled
                )
            }
        }
    }

    fun changePassword() {
        viewModelScope.launch(Dispatchers.IO) {
            val oldPassword = _settingsUiState.value.oldPassword ?: return@launch
            val newPassword = _settingsUiState.value.newPassword ?: return@launch

            _settingsUiState.update {
                it.copy(isLoading = true)
            }

            when(val result = changeUserPasswordUseCase.invoke(oldPassword, newPassword)) {
                is Result.Success -> {
                    _changePasswordEvent.emit(ChangePasswordEvent.Success)
                }
                is Result.Error -> {
                    _changePasswordEvent.emit(ChangePasswordEvent.ShowError(result.error))
                }
            }

            _settingsUiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = logoutUserUseCase.invoke()) {
                is Result.Success -> {
                    _logoutEvent.emit(LogoutUserEvent.Success)
                }
                is Result.Error -> {
                    _logoutEvent.emit(LogoutUserEvent.ShowError(result.error))
                }
            }
        }
    }
}