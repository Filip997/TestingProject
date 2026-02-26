package com.example.localinformant.core.domain.repositories

import android.net.Uri
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.result.Result

interface FirebaseStorageRepository {

    suspend fun savePostImages(postId: String, uriImages: List<Uri>): Result<List<String>, NetworkError>
}