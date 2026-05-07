package com.example.localinformant.home.domain.repositories

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Post
import com.example.localinformant.core.domain.result.Result

interface HomeRepository {

    suspend fun getPostsByFollowedCompanies(followedUsersIds: List<String>, isRefreshing: Boolean): Result<List<Post>, NetworkError>
    suspend fun getPostById(postId: String): Result<Post, NetworkError>
    suspend fun getCompanyByPostId(postId: String): Result<Company, NetworkError>
}