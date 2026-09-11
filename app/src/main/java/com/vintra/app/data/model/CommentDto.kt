package com.vintra.app.data.model

import com.google.firebase.firestore.PropertyName

data class CommentDto @JvmOverloads constructor(
    val postId: String = "",
    val authorUid: String = "",
    val authorName: String = "",
    val authorUsername: String = "",
    val authorPhotoBase64: String? = null,
    val text: String = "",
    val createdAt: Long = 0L,
    @get:PropertyName("isVerified")
    @set:PropertyName("isVerified")
    var isVerified: Boolean = false
)