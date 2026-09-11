package com.vintra.app.domain.model

data class Comment(
    val id: String,
    val postId: String,
    val authorUid: String,
    val authorName: String,
    val authorUsername: String,
    val authorPhotoBase64: String?,
    val text: String,
    val createdAt: Long,
    val isVerified: Boolean = false
)