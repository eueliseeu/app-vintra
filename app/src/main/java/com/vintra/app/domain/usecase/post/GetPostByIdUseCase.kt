package com.vintra.app.domain.usecase.post

import com.vintra.app.domain.model.Post
import com.vintra.app.domain.repository.PostRepository
import javax.inject.Inject

class GetPostByIdUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String): Post? = postRepository.getPostById(postId)
}