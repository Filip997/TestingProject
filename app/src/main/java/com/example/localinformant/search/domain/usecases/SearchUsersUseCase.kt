package com.example.localinformant.search.domain.usecases

import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.search.domain.repositories.SearchRepository
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {

    suspend operator fun invoke(searchQuery: String): List<User> {
        val persons = searchRepository.searchPersonsByName(searchQuery)
        val companies = searchRepository.searchCompaniesByName(searchQuery)

        val allUsers = mutableListOf<User>()

        if (persons is Result.Success) {
            allUsers.addAll(persons.data)
        }

        if (companies is Result.Success) {
            allUsers.addAll(companies.data)
        }

        return allUsers
    }
}