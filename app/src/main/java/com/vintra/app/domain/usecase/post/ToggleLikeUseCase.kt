package com.vintra.app.domain.usecase.post

import com.vintra.app.domain.repository.PostRepository
import com.vintra.app.domain.repository.ToggleLikeResult
import javax.inject.Inject

class ToggleLikeUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String, uid: String): ToggleLikeResult =
        postRepository.toggleLike(postId, uid)
}