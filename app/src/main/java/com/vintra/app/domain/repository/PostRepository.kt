package com.vintra.app.domain.repository

import com.vintra.app.domain.model.AuthProvider
import com.vintra.app.domain.model.Post
import kotlinx.coroutines.flow.Flow

sealed interface CreatePostResult {
    data class Success(val postId: String) : CreatePostResult
    data class Error(val message: String) : CreatePostResult
}

sealed interface ObservePostsResult {
    data class Success(val posts: List<Post>) : ObservePostsResult
    data class Error(val message: String) : ObservePostsResult
}

sealed interface ToggleLikeResult {
    data object Success : ToggleLikeResult
    data class Error(val message: String) : ToggleLikeResult
}

interface PostRepository {
    suspend fun createPost(
        authorUid: String,
        authorName: String,
        authorUsername: String,
        authorPhotoBase64: String?,
        authorProvider: AuthProvider,
        title: String,
        text: String,
        linkUrl: String,
        imageBase64: String?,
        isVerified: Boolean
    ): CreatePostResult

    fun observeFeed(limit: Long = 30): Flow<ObservePostsResult>

    suspend fun getPostById(postId: String): Post?

    suspend fun toggleLike(postId: String, uid: String): ToggleLikeResult
}