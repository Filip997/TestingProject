package com.example.localinformant.account.domain.repositories

import android.net.Uri
import com.example.localinformant.account.domain.error.ChangePassError
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Post
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.domain.result.Result

interface UserAccountRepository {

    suspend fun getUsersByIds(userIds: List<String>): Result<List<User>, NetworkError>

    suspend fun followCompany(currentUserType: UserType, userId: String): Result<User, NetworkError>
    suspend fun unfollowCompany(currentUserType: UserType, userId: String): Result<User, NetworkError>

    suspend fun saveProfileImage(imageUri: Uri, userType: UserType): Result<String, NetworkError>
    suspend fun setProfileImage(imageUrl: String, userType: UserType): Result<User, NetworkError>

    suspend fun getPostsByUserId(userId: String, isRefreshing: Boolean): Result<List<Post>, NetworkError>
    suspend fun getPostIdsFromUserReactions(userId: String): Result<List<String>, NetworkError>
    suspend fun getPostIdsFromUserComments(userId: String): Result<List<String>, NetworkError>
    suspend fun getPostsByPostIds(postIds: List<String>, isRefreshing: Boolean): Result<List<Post>, NetworkError>
    suspend fun getCompanyByPostId(postId: String): Result<Company, NetworkError>

    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit, ChangePassError>

    suspend fun logout(): Result<Unit, NetworkError>
}