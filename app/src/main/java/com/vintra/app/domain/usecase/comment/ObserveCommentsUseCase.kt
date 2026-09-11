package com.vintra.app.domain.usecase.comment

import com.vintra.app.domain.repository.CommentRepository
import com.vintra.app.domain.repository.ObserveCommentsResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    operator fun invoke(postId: String, limit: Long = 50): Flow<ObserveCommentsResult> =
        commentRepository.observeComments(postId, limit)
}