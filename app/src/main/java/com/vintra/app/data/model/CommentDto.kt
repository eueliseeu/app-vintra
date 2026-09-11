package com.vintra.app.data.model

data class CommentDto @JvmOverloads constructor(
    val postId: String = "",
    val authorUid: String = "",
    val authorName: String = "",
    val authorUsername: String = "",
    val authorPhotoBase64: String? = null,
    val text: String = "",
    val createdAt: Long = 0L,
    val isVerified: Boolean = false
)