package com.vintra.app.domain.usecase.post

import com.vintra.app.domain.repository.DeletePostResult
import com.vintra.app.domain.repository.PostRepository
import javax.inject.Inject

class DeletePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String, requesterUid: String): DeletePostResult =
        postRepository.deletePost(postId, requesterUid)
}