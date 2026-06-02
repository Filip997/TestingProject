package com.example.localinformant.conversations.domain.usecases

import com.example.localinformant.conversations.domain.repositories.ConversationsRepository
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.result.Result
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val conversationsRepository: ConversationsRepository
) {

    suspend operator fun invoke(searchQuery: String): List<User> {
        val persons = conversationsRepository.searchPersonsByName(searchQuery)
        val companies = conversationsRepository.searchCompaniesByName(searchQuery)

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