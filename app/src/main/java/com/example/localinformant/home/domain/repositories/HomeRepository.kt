package com.example.localinformant.home.domain.repositories

import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Comment
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Post
import com.example.localinformant.core.domain.models.Reaction
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.result.Result
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    suspend fun getPostsByFollowedCompanies(followedUsersIds: List<String>, isRefreshing: Boolean): Result<List<Post>, NetworkError>
    suspend fun getCompanyByPostId(postId: String): Result<Company, NetworkError>

    suspend fun submitReaction(id: String, postId: String, user: User, userType: UserType): Result<Unit, NetworkError>
    fun observeReactions(postIds: List<String>): Flow<List<Reaction>>

    suspend fun submitComment(id: String, postId: String, commentText: String, user: User, userType: UserType): Result<Unit, NetworkError>
    fun observeComments(postIds: List<String>): Flow<List<Comment>>
}