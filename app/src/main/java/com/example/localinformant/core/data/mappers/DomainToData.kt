package com.example.localinformant.core.data.mappers

import com.example.localinformant.core.data.dto.CommentDto
import com.example.localinformant.core.data.dto.CompanyDto
import com.example.localinformant.core.data.dto.PersonDto
import com.example.localinformant.core.data.dto.PostDto
import com.example.localinformant.core.data.dto.ReactionDto
import com.example.localinformant.core.domain.models.Comment
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.Post
import com.example.localinformant.core.domain.models.Reaction
import com.google.firebase.Timestamp

fun Post.toDto(): PostDto {
    return PostDto(
        id = id,
        createdAt = Timestamp(createdAt / 1000, ((createdAt % 1000) * 1_000_000).toInt()),
        userId = userId,
        postText = postText,
        imageUrls = imageUrls,
        likes = likes.map { it.id },
        comments = comments.map { it.id }
    )
}

fun Company.toDto(): CompanyDto {
    return CompanyDto(
        id = id,
        companyName = companyName,
        companyNameLowerCase = companyName.lowercase(),
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

fun Person.toDto(): PersonDto {
    return PersonDto(
        id = id,
        firstName = firstName,
        firstNameLowerCase = firstName.lowercase(),
        lastName = lastName,
        lastNameLowerCase = lastName.lowercase(),
        email = email,
        token = token,
        following = following
    )
}

fun Comment.toDto(): CommentDto {
    return CommentDto(
        id = id,
        createdAt = Timestamp(createdAt / 1000, ((createdAt % 1000) * 1_000_000).toInt()),
        userId = userId,
        userType = userType?.name ?: "",
        userProfileImage = userProfileImage,
        userName = userName,
        postId = postId,
        commentText = commentText
    )
}

fun Reaction.toDto(): ReactionDto {
    return ReactionDto(
        id = id,
        userId = userId,
        userType = userType?.name ?: "",
        userProfileImage = userProfileImage,
        userName = userName,
        postId = postId
    )
}