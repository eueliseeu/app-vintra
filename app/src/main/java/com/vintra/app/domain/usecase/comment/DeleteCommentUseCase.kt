package com.vintra.app.domain.usecase.comment

import com.vintra.app.domain.repository.CommentRepository
import com.vintra.app.domain.repository.DeleteCommentResult
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(
        commentId: String,
        postId: String,
        requesterUid: String,
        postAuthorUid: String
    ): DeleteCommentResult = commentRepository.deleteComment(
        commentId, postId, requesterUid, postAuthorUid
    )
}