package com.example.localinformant.core.data.mappers

import com.example.localinformant.core.data.dto.CommentDto
import com.example.localinformant.core.data.dto.CompanyDto
import com.example.localinformant.core.data.dto.ConversationDto
import com.example.localinformant.core.data.dto.MessageDto
import com.example.localinformant.core.data.dto.NotificationDto
import com.example.localinformant.core.data.dto.PersonDto
import com.example.localinformant.core.data.dto.PostDto
import com.example.localinformant.core.data.dto.ReactionDto
import com.example.localinformant.core.domain.models.Comment
import com.example.localinformant.core.domain.models.Post
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Conversation
import com.example.localinformant.core.domain.models.Message
import com.example.localinformant.core.domain.models.Notification
import com.example.localinformant.core.domain.models.NotificationType
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.Reaction
import com.example.localinformant.core.domain.models.UserStatus
import com.example.localinformant.core.domain.models.UserType
import kotlin.String

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
        status = UserStatus.valueOf(status),
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
        status = UserStatus.valueOf(status),
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

fun ConversationDto.toDomain(currentUserId: String): Conversation {
    return Conversation(
        id = id,
        participants = participants,
        participant2Id = participants.firstOrNull { it != currentUserId } ?: "",
        participant2Name = "",
        participant2ProfileImage = "",
        messages = messages,
        lastMessage = lastMessage,
        lastMessageUserId = lastMessageUserId,
        lastMessageTime = lastMessageTime?.toDate()?.time ?: 0L
    )
}

fun MessageDto.toDomain(): Message {
    return Message(
        id = id,
        conversationId = conversationId,
        senderId = senderId,
        receiverId = receiverId,
        content = content,
        timeSent = timeSent?.toDate()?.time ?: 0L
    )
}