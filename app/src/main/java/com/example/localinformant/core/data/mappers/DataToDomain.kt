package com.example.localinformant.core.data.mappers

import com.example.localinformant.core.data.dto.CommentDto
import com.example.localinformant.core.data.dto.CompanyDto
import com.example.localinformant.core.data.dto.NotificationDto
import com.example.localinformant.core.data.dto.PersonDto
import com.example.localinformant.core.data.dto.PostDto
import com.example.localinformant.core.data.dto.ReactionDto
import com.example.localinformant.core.domain.models.Comment
import com.example.localinformant.core.domain.models.Post
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Notification
import com.example.localinformant.core.domain.models.NotificationType
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.Reaction
import com.example.localinformant.core.domain.models.UserType

fun PostDto.toDomain(): Post {
    return Post(
        id = id,
        createdAt = createdAt?.toDate()?.time ?: 0L,
        userId = userId,
        postText = postText,
        imageUrls = imageUrls,
        likes = likes.map { Reaction(id = it) },
        comments = comments.map { Comment(id = it) }
    )
}

fun CompanyDto.toDomain(): Company {
    return Company(
        id = id,
        companyName = companyName,
        companyProfileImageUrl = companyProfileImageUrl,
        companyEmail = companyEmail,
        email = email,
        firstName = firstName,
        lastName = lastName,
        token = token,
        followers = followers,
        following = following,
        posts = posts
    )
}

fun PersonDto.toDomain(): Person {
    return Person(
        id = id,
        profileImageUrl = profileImageUrl,
        firstName = firstName,
        lastName = lastName,
        fullName = "$firstName $lastName",
        email = email,
        token = token,
        following = following
    )
}

fun CommentDto.toDomain(): Comment {
    return Comment(
        id = id,
        createdAt = createdAt?.toDate()?.time ?: 0L,
        userId = userId,
        userType = UserType.valueOf(userType),
        userProfileImage = "",
        userName = "",
        postId = postId,
        commentText = commentText
    )
}

fun ReactionDto.toDomain(): Reaction {
    return Reaction(
        id = id,
        userId = userId,
        userType = UserType.valueOf(userType),
        postId = postId
    )
}

fun NotificationDto.toDomain(): Notification {
    return Notification(
        id = id,
        createdOn = createdOn?.toDate()?.time ?: 0L,
        toUserId = toUserId,
        fromUser = when(UserType.valueOf(fromUserType)) {
            UserType.PERSON -> Person(id = fromUserId)
            UserType.COMPANY -> Company(id = fromUserId)
        },
        fromUserType = UserType.valueOf(fromUserType),
        postId = postId,
        notificationType = NotificationType.valueOf(notificationType)
    )
}