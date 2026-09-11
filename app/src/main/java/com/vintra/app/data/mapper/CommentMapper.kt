package com.vintra.app.data.mapper

import com.vintra.app.data.model.CommentDto
import com.vintra.app.domain.model.Comment

fun CommentDto.toDomain(id: String): Comment = Comment(
    id = id,
    postId = postId,
    authorUid = authorUid,
    authorName = authorName,
    authorUsername = authorUsername,
    authorPhotoBase64 = authorPhotoBase64,
    text = text,
    createdAt = createdAt,
    isVerified = isVerified
)