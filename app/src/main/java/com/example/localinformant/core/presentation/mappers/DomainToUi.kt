package com.example.localinformant.core.presentation.mappers

import com.example.localinformant.core.domain.models.Comment
import com.example.localinformant.core.domain.models.PostWithCompany
import com.example.localinformant.core.domain.models.Reaction
import com.example.localinformant.core.presentation.models.CommentUi
import com.example.localinformant.core.presentation.models.PostUiState
import com.example.localinformant.core.presentation.models.ReactionUi

fun PostWithCompany.toUi(): PostUiState {
    return PostUiState(
        id = this.post.id,
        createdAt = this.post.createdAt,
        companyId = this.company?.id ?: "",
        companyName = this.company?.companyName ?: "",
        companyProfileImageUrl = this.company?.companyProfileImageUrl ?: "",
        postText = this.post.postText,
        postImageUrls = this.post.imageUrls,
        likeBtnClicked = this.post.likeBtnClicked,
        postLikes = this.post.likes.map { it.toUi() },
        postComments = this.post.comments.map { it.toUi() },
        commentSectionVisible = false
    )
}

fun Comment.toUi(): CommentUi {
    return CommentUi(
        id = id,
        createdAt = createdAt,
        userId = userId,
        userType = userType,
        userProfileImage = userProfileImage,
        userName = userName,
        commentText = commentText
    )
}

fun Reaction.toUi(): ReactionUi {
    return ReactionUi(
        id = id,
        userId = userId,
        userType = userType,
        userProfileImage = userProfileImage,
        userName = userName
    )
}