package com.example.localinformant.core.domain.repositories

import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Person

interface GlobalRepository {

    suspend fun updatePersonToken(token: String): Person?
    suspend fun updateCompanyToken(token: String): Company?
}