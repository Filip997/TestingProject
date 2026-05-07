package com.example.localinformant.account.presentation.models

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.presentation.models.UserAccountDetailsUi

data class UserAccountUiState(
    val isLoading: Boolean = false,
    val isLoadingFollowRequest: Boolean = false,
    val userAccountDetails: UserAccountDetailsUi = UserAccountDetailsUi(),
    val error: NetworkError? = null
)
