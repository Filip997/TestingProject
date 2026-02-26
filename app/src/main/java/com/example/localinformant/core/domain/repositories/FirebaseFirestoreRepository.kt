package com.example.localinformant.core.domain.repositories

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.result.Result

interface FirebaseFirestoreRepository {

    suspend fun savePost(id: String, postText: String, imageUrls: List<String>): Result<Unit, NetworkError>

    suspend fun updatePersonToken(token: String)
    suspend fun updateCompanyToken(token: String)
}