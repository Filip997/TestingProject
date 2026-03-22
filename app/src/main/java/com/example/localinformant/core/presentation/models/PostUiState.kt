package com.example.localinformant.core.presentation.models

data class PostUiState(
    val id: String,
    val createdAt: Long,
    val companyId: String,
    val companyName: String,
    val companyProfileImageUrl: String,
    val postText: String,
    val postImageUrls: List<String>,
    val likeBtnClicked: Boolean,
    val postLikes: List<ReactionUi>,
    val postComments: List<CommentUi>,
    val commentSectionVisible: Boolean
)
