package com.example.localinformant.main.domain.repositories

import android.net.Uri
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.result.Result

interface MainRepository {

    suspend fun savePostImages(postId: String, uriImages: List<Uri>): Result<List<String>, NetworkError>
    suspend fun savePost(id: String, postText: String, imageUrls: List<String>): Result<Company, NetworkError>
}