package com.vintra.app.domain.usecase.post

import com.vintra.app.domain.repository.ObservePostsResult
import com.vintra.app.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFeedUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(limit: Long = 30): Flow<ObservePostsResult> = postRepository.observeFeed(limit)
}