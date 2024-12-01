package com.example.localinformant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localinformant.models.CreatePostResponse
import com.example.localinformant.models.Post
import com.example.localinformant.models.PostRequest
import com.example.localinformant.repositories.PostRepository
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val postRepository = PostRepository()

    private val createPostMutable = MutableLiveData<CreatePostResponse>()
    val createPostLiveData: LiveData<CreatePostResponse> = createPostMutable

    private val currentCompanyPostsMutable = MutableLiveData<List<Post>>()
    val currentCompanyPostsLiveData: LiveData<List<Post>> = currentCompanyPostsMutable

    private val _currentPersonsFollowedCompaniesPosts = MutableLiveData<List<Post>>()
    val currentPersonsFollowedCompaniesPosts: LiveData<List<Post>> = _currentPersonsFollowedCompaniesPosts

    private val isLoadingMutable = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoadingMutable

    fun createPost(postRequest: PostRequest) {
        viewModelScope.launch {
            isLoadingMutable.postValue(true)
            val response = postRepository.createPost(postRequest)
            createPostMutable.postValue(response)
            isLoadingMutable.postValue(false)
        }
    }

    fun getCurrentCompanyPosts() {
        viewModelScope.launch {
            val posts = postRepository.getCurrentCompanyPosts()
            currentCompanyPostsMutable.postValue(posts)
        }
    }

    fun getAllPostsByFollowedCompaniesFromCurrentPerson() {
        viewModelScope.launch {
            val posts = postRepository.getAllPostsByFollowedCompaniesFromCurrentPerson()
            _currentPersonsFollowedCompaniesPosts.postValue(posts)
        }
    }
}