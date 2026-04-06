package com.example.localinformant.core.presentation.mappers

import com.example.localinformant.core.domain.models.Comment
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Notification
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.PostWithCompany
import com.example.localinformant.core.domain.models.Reaction
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.presentation.models.CommentUi
import com.example.localinformant.core.presentation.models.NotificationUi
import com.example.localinformant.core.presentation.models.PostUiState
import com.example.localinformant.core.presentation.models.ReactionUi
import com.example.localinformant.core.presentation.models.SearchedUserUi

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

fun Person.toSearchedUserUi(): SearchedUserUi {
    return SearchedUserUi(
        id = id,
        userType = UserType.PERSON,
        userName = fullName,
        userProfileImageUrl = profileImageUrl
    )
}

fun Company.toSearchedUserUi(): SearchedUserUi {
    return SearchedUserUi(
        id = id,
        userType = UserType.COMPANY,
        userName = companyName,
        userProfileImageUrl = companyProfileImageUrl
    )
}

fun Notification.toUi(): NotificationUi {
    return NotificationUi(
        id = id,
        createdOn = createdOn,
        toUserId = toUserId,
        fromUserId = when(fromUserType!!) {
            UserType.PERSON -> (fromUser as Person).id
            UserType.COMPANY -> (fromUser as Company).id
        },
        fromUserType = fromUserType,
        fromUserName = when(fromUserType) {
            UserType.PERSON -> (fromUser as Person).fullName
            UserType.COMPANY -> (fromUser as Company).companyName
        },
        fromUserProfileImageUrl = when(fromUserType) {
            UserType.PERSON -> (fromUser as Person).profileImageUrl
            UserType.COMPANY -> (fromUser as Company).companyProfileImageUrl
        },
        postId = postId,
        notificationType = notificationType
    )
}