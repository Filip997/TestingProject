package com.example.localinformant.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.auth.domain.usecases.GetUserTypeUseCase
import com.example.localinformant.auth.domain.usecases.RegisterUserData
import com.example.localinformant.auth.domain.usecases.RegisterUserUseCase
import com.example.localinformant.auth.domain.error.AuthError
import com.example.localinformant.auth.domain.validators.RegisterValidator
import com.example.localinformant.auth.presentation.events.RegisterEvent
import com.example.localinformant.auth.presentation.models.RegisterUiState
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.core.domain.models.UserType
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
class RegisterViewModel @Inject constructor(
    private val registerValidator: RegisterValidator,
    private val getUserTypeUseCase: GetUserTypeUseCase,
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private var userType: UserType? = null

    private val _registerUiState: MutableStateFlow<RegisterUiState> =
        MutableStateFlow(RegisterUiState())
    val registerUiState = _registerUiState
        .onStart { loadUserType() }
        .stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000L),
            RegisterUiState()
        )

    private val _registerEvent: MutableSharedFlow<RegisterEvent> = MutableSharedFlow()
    val registerEvent = _registerEvent.asSharedFlow()


    fun loadUserType() {
        viewModelScope.launch(Dispatchers.IO) {
            userType = getUserTypeUseCase.invoke()
            _registerUiState.update {
                it.copy(
                    userType = userType
                )
            }
        }
    }

    fun onRegisterClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            _registerUiState.update {
                it.copy(
                    isLoading = true
                )
            }

            val uiState = _registerUiState.value

            val input = when(userType!!) {
                UserType.PERSON -> {
                    RegisterUserData.Person(
                        firstName = uiState.firstName!!,
                        lastName = uiState.lastName!!,
                        email = uiState.email!!,
                        password = uiState.password!!
                    )
                }
                UserType.COMPANY -> {
                    RegisterUserData.Company(
                        companyName = uiState.companyName!!,
                        companyEmail = uiState.companyEmail!!,
                        firstName = uiState.firstName!!,
                        lastName = uiState.lastName!!,
                        email = uiState.email!!,
                        password = uiState.password!!
                    )
                }
            }

            when(val registerResult = registerUserUseCase.invoke(input)) {
                is Result.Success -> {
                    _registerEvent.emit(RegisterEvent.NavigateToLogin)
                }
                is Result.Error -> {
                    _registerEvent.emit(RegisterEvent.ShowError(registerResult.error as AuthError))
                }
            }

            _registerUiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun onCompanyNameChange(companyName: String) {
        viewModelScope.launch {
            val companyNameError = registerValidator.validateCompanyName(companyName)
            _registerUiState.update {
                val isRegistrationEnabled = registerValidator.validateIfRegistrationIsEnabled(
                    userType = userType!!,
                    companyNameError = companyNameError,
                    companyEmailError = it.companyEmailError,
                    firstNameError = it.firstNameError,
                    lastNameError = it.lastNameError,
                    emailError = it.emailError,
                    passwordError = it.passwordError,
                    confirmPasswordError = it.confirmPasswordError,
                    isAgreementChecked = it.isAgreementChecked
                )

                it.copy(
                    companyName = companyName,
                    companyNameError = companyNameError,
                    isRegistrationEnabled = isRegistrationEnabled
                )
            }
        }
    }

    fun onCompanyEmailChange(companyEmail: String) {
        viewModelScope.launch {
            val companyEmailError = registerValidator.validateCompanyEmail(companyEmail)
            _registerUiState.update {
                val isRegistrationEnabled = registerValidator.validateIfRegistrationIsEnabled(
                    userType = userType!!,
                    companyNameError = it.companyNameError,
                    companyEmailError = companyEmailError,
                    firstNameError = it.firstNameError,
                    lastNameError = it.lastNameError,
                    emailError = it.emailError,
                    passwordError = it.passwordError,
                    confirmPasswordError = it.confirmPasswordError,
                    isAgreementChecked = it.isAgreementChecked
                )

                it.copy(
                    companyEmail = companyEmail,
                    companyEmailError = companyEmailError,
                    isRegistrationEnabled = isRegistrationEnabled
                )
            }
        }
    }

    fun onFirstNameChange(firstName: String) {
        viewModelScope.launch {
            val firstNameError = registerValidator.validateFirstName(firstName)
            _registerUiState.update {
                val isRegistrationEnabled = registerValidator.validateIfRegistrationIsEnabled(
                    userType = userType!!,
                    companyNameError = it.companyNameError,
                    companyEmailError = it.companyEmailError,
                    firstNameError = firstNameError,
                    lastNameError = it.lastNameError,
                    emailError = it.emailError,
                    passwordError = it.passwordError,
                    confirmPasswordError = it.confirmPasswordError,
                    isAgreementChecked = it.isAgreementChecked
                )

                it.copy(
                    firstName = firstName,
                    firstNameError = firstNameError,
                    isRegistrationEnabled = isRegistrationEnabled
                )
            }
        }
    }

    fun onLastNameChange(lastName: String) {
        viewModelScope.launch {
            val lastNameError = registerValidator.validateLastName(lastName)
            _registerUiState.update {
                val isRegistrationEnabled = registerValidator.validateIfRegistrationIsEnabled(
                    userType = userType!!,
                    companyNameError = it.companyNameError,
                    companyEmailError = it.companyEmailError,
                    firstNameError = it.firstNameError,
                    lastNameError = lastNameError,
                    emailError = it.emailError,
                    passwordError = it.passwordError,
                    confirmPasswordError = it.confirmPasswordError,
                    isAgreementChecked = it.isAgreementChecked
                )

                it.copy(
                    lastName = lastName,
                    lastNameError = lastNameError,
                    isRegistrationEnabled = isRegistrationEnabled
                )
            }
        }
    }

    fun onEmailChange(email: String) {
        viewModelScope.launch {
            val emailError = registerValidator.validateEmail(email)
            _registerUiState.update {
                val isRegistrationEnabled = registerValidator.validateIfRegistrationIsEnabled(
                    userType = userType!!,
                    companyNameError = it.companyNameError,
                    companyEmailError = it.companyEmailError,
                    firstNameError = it.firstNameError,
                    lastNameError = it.lastNameError,
                    emailError = emailError,
                    passwordError = it.passwordError,
                    confirmPasswordError = it.confirmPasswordError,
                    isAgreementChecked = it.isAgreementChecked
                )

                it.copy(
                    email = email,
                    emailError = emailError,
                    isRegistrationEnabled = isRegistrationEnabled
                )
            }
        }
    }

    fun onPasswordChange(password: String) {
        viewModelScope.launch {
            val passwordError = registerValidator.validatePassword(password)
            _registerUiState.update {
                val isRegistrationEnabled = registerValidator.validateIfRegistrationIsEnabled(
                    userType = userType!!,
                    companyNameError = it.companyNameError,
                    companyEmailError = it.companyEmailError,
                    firstNameError = it.firstNameError,
                    lastNameError = it.lastNameError,
                    emailError = it.emailError,
                    passwordError = passwordError,
                    confirmPasswordError = it.confirmPasswordError,
                    isAgreementChecked = it.isAgreementChecked
                )

                it.copy(
                    password = password,
                    passwordError = passwordError,
                    isRegistrationEnabled = isRegistrationEnabled
                )
            }
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        viewModelScope.launch {
            val confirmPasswordError = registerValidator.validateConfirmPassword(confirmPassword)
            _registerUiState.update {
                val isRegistrationEnabled = registerValidator.validateIfRegistrationIsEnabled(
                    userType = userType!!,
                    companyNameError = it.companyNameError,
                    companyEmailError = it.companyEmailError,
                    firstNameError = it.firstNameError,
                    lastNameError = it.lastNameError,
                    emailError = it.emailError,
                    passwordError = it.passwordError,
                    confirmPasswordError = confirmPasswordError,
                    isAgreementChecked = it.isAgreementChecked
                )

                it.copy(
                    confirmPassword = confirmPassword,
                    confirmPasswordError = confirmPasswordError,
                    isRegistrationEnabled = isRegistrationEnabled
                )
            }
        }
    }

    fun onAgreementChecked(isChecked: Boolean) {
        viewModelScope.launch {
            _registerUiState.update {
                val isRegistrationEnabled = registerValidator.validateIfRegistrationIsEnabled(
                    userType = userType!!,
                    companyNameError = it.companyNameError,
                    companyEmailError = it.companyEmailError,
                    firstNameError = it.firstNameError,
                    lastNameError = it.lastNameError,
                    emailError = it.emailError,
                    passwordError = it.passwordError,
                    confirmPasswordError = it.confirmPasswordError,
                    isAgreementChecked = isChecked
                )

                it.copy(
                    isAgreementChecked = isChecked,
                    isRegistrationEnabled = isRegistrationEnabled
                )
            }
        }
    }
}