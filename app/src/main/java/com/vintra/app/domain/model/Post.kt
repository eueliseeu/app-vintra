package com.vintra.app.domain.model

data class Post(
    val id: String,
    val authorUid: String,
    val authorName: String,
    val authorUsername: String,
    val authorPhotoBase64: String?,
    val authorProvider: AuthProvider,
    val title: String = "",
    val text: String,
    val linkUrl: String = "",
    val imageBase64: String?,
    val createdAt: Long,
    val commentCount: Int = 0,
    val isVerified: Boolean = false,
    val likedBy: List<String> = emptyList(),
    val likeCount: Int = 0
)