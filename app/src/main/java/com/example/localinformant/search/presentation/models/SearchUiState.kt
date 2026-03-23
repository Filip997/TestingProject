package com.example.localinformant.search.presentation.models

import com.example.localinformant.core.presentation.models.SearchedUserUi

data class SearchUiState(
    val isLoading: Boolean = false,
    val searchedUsers: List<SearchedUserUi> = listOf()
)
