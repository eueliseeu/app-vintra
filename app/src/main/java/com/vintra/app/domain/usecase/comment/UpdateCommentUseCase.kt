package com.vintra.app.domain.usecase.comment

import com.vintra.app.domain.repository.CommentRepository
import com.vintra.app.domain.repository.UpdateCommentResult
import javax.inject.Inject

class UpdateCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(
        commentId: String,
        editorUid: String,
        text: String
    ): UpdateCommentResult =
        commentRepository.updateComment(commentId, editorUid, text)
}