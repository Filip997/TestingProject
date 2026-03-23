package com.example.localinformant.search.domain.repositories

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.result.Result

interface SearchRepository {

    suspend fun searchPersonsByName(searchQuery: String): Result<List<Person>, NetworkError>
    suspend fun searchCompaniesByName(searchQuery: String): Result<List<Company>, NetworkError>
}