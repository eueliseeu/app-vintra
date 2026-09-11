package com.vintra.app.domain.repository

import com.vintra.app.domain.model.Comment
import kotlinx.coroutines.flow.Flow

sealed interface CreateCommentResult {
    data class Success(val commentId: String) : CreateCommentResult
    data class Error(val message: String) : CreateCommentResult
}

sealed interface ObserveCommentsResult {
    data class Success(val comments: List<Comment>) : ObserveCommentsResult
    data class Error(val message: String) : ObserveCommentsResult
}

interface CommentRepository {
    suspend fun createComment(
        postId: String,
        authorUid: String,
        authorName: String,
        authorUsername: String,
        authorPhotoBase64: String?,
        text: String,
        isVerified: Boolean
    ): CreateCommentResult

    fun observeComments(postId: String, limit: Long = 50): Flow<ObserveCommentsResult>
}