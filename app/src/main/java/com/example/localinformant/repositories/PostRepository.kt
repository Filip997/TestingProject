package com.example.localinformant.repositories

import com.example.localinformant.constants.AppConstants
import com.example.localinformant.constants.PreferencesManager
import com.example.localinformant.models.Comment
import com.example.localinformant.models.Company
import com.example.localinformant.models.CreatePostResponse
import com.example.localinformant.core.domain.models.Post
import com.example.localinformant.models.PostRequest
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PostRepository {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun createPost(request: PostRequest): CreatePostResponse {
        try {
            val currentUserId = auth.currentUser?.uid!!

            val currentCompany = db.collection(AppConstants.COMPANIES).document(currentUserId).get().await().toObject(Company::class.java)

            val post = hashMapOf(
                AppConstants.ID to request.id,
                AppConstants.COMPANY_ID to currentUserId,
                AppConstants.COMPANY_NAME to currentCompany?.companyName,
                AppConstants.POST_TEXT to request.postText,
                AppConstants.LIKES to listOf<String>(),
                AppConstants.COMMENTS to listOf<String>()
            )

            db.collection(AppConstants.POSTS).document(request.id).set(post).await()
            return CreatePostResponse(true, "Post successfully created")
        } catch (e: Exception) {
            return CreatePostResponse(false, e.message.toString())
        }
    }

    suspend fun getCurrentCompanyPosts(): List<Post> {
        try {
            val currentUserId = auth.currentUser?.uid!!

            val postList = db.collection(AppConstants.POSTS).whereEqualTo(AppConstants.COMPANY_ID, currentUserId).get().await()
                .toObjects(Post::class.java)

            return postList
        } catch (e: Exception) {
            return listOf()
        }
    }

    suspend fun getPostsByCompanyId(companyId: String): List<Post> {
        try {
            val postList = db.collection(AppConstants.POSTS).whereEqualTo(AppConstants.COMPANY_ID, companyId).get().await()
                .toObjects(Post::class.java)

            return postList
        } catch (e: Exception) {
            return listOf()
        }
    }

    suspend fun getAllPostsByFollowedCompaniesFromCurrentPerson(): List<Post> {
        try {
            val allPosts = mutableListOf<Post>()
            val currentPerson = PreferencesManager.getPerson()

            if (currentPerson != null) {
                for (companyId in currentPerson.following) {
                    val postList = db.collection(AppConstants.POSTS)
                        .whereEqualTo(AppConstants.COMPANY_ID, companyId).get().await()
                        .toObjects(Post::class.java)

                    allPosts.addAll(postList)
                }
            }

            return allPosts
        } catch (e: Exception) {
            return listOf()
        }
    }
}