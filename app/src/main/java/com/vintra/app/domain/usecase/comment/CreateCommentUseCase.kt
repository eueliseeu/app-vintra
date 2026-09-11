package com.vintra.app.domain.usecase.comment

import com.vintra.app.domain.repository.CommentRepository
import com.vintra.app.domain.repository.CreateCommentResult
import javax.inject.Inject

class CreateCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(
        postId: String,
        authorUid: String,
        authorName: String,
        authorUsername: String,
        authorPhotoBase64: String?,
        text: String,
        isVerified: Boolean
    ): CreateCommentResult {
        if (text.isBlank()) {
            return CreateCommentResult.Error("Empty comment.")
        }
        return commentRepository.createComment(
            postId = postId,
            authorUid = authorUid,
            authorName = authorName,
            authorUsername = authorUsername,
            authorPhotoBase64 = authorPhotoBase64,
            text = text,
            isVerified = isVerified
        )
    }
}