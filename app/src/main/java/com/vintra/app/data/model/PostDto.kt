package com.vintra.app.data.model

import com.google.firebase.firestore.PropertyName

data class PostDto @JvmOverloads constructor(
    val authorUid: String = "",
    val authorName: String = "",
    val authorUsername: String = "",
    val authorPhotoBase64: String? = null,
    val authorProvider: String = "",
    val title: String = "",
    val text: String = "",
    val linkUrl: String = "",
    val imageBase64: String? = null,
    val createdAt: Long = 0L,
    val commentCount: Int = 0,
    @get:PropertyName("isVerified")
    @set:PropertyName("isVerified")
    var isVerified: Boolean = false,
    val likedBy: List<String> = emptyList(),
    val likeCount: Int = 0
)