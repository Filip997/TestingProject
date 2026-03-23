package com.example.localinformant.search.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.presentation.mappers.toSearchedUserUi
import com.example.localinformant.core.presentation.models.SearchedUserUi
import com.example.localinformant.search.domain.usecases.SearchUsersUseCase
import com.example.localinformant.search.presentation.models.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUsersUseCase: SearchUsersUseCase
) : ViewModel() {

    private val _searchUiState: MutableStateFlow<SearchUiState> = MutableStateFlow(SearchUiState())
    val searchUiState = _searchUiState.asStateFlow()

    private var searchUsersJob: Job? = null

    fun searchUsersByName(searchQuery: String) {
        searchUsersJob?.cancel()

        searchUsersJob = viewModelScope.launch(Dispatchers.IO) {
            _searchUiState.update {
                it.copy(isLoading = true)
            }

            val users = searchUsersUseCase.invoke(searchQuery)
            val searchedUsers: List<SearchedUserUi> = users.map {
                when(it) {
                    is Person -> it.toSearchedUserUi()
                    is Company -> it.toSearchedUserUi()
                    else -> SearchedUserUi()
                }
            }

            _searchUiState.update {
                it.copy(
                    searchedUsers = searchedUsers
                )
            }

            _searchUiState.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }
}