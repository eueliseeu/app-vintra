package com.vintra.app.data.mapper

import com.vintra.app.data.model.PostDto
import com.vintra.app.domain.model.AuthProvider
import com.vintra.app.domain.model.Post

fun PostDto.toDomain(id: String): Post = Post(
    id = id,
    authorUid = authorUid,
    authorName = authorName,
    authorUsername = authorUsername,
    authorPhotoBase64 = authorPhotoBase64,
    authorProvider = runCatching { AuthProvider.valueOf(authorProvider) }.getOrDefault(AuthProvider.UNKNOWN),
    title = title,
    text = text,
    linkUrl = linkUrl,
    imageBase64 = imageBase64,
    createdAt = createdAt,
    commentCount = commentCount,
    isVerified = isVerified,
    likedBy = likedBy,
    likeCount = likeCount
)