package com.vintra.app.domain.usecase.post

import android.net.Uri
import com.vintra.app.domain.model.AuthProvider
import com.vintra.app.domain.repository.CreatePostResult
import com.vintra.app.domain.repository.PostRepository
import com.vintra.app.domain.service.ImageProcessResult
import com.vintra.app.domain.service.ImageProcessor
import javax.inject.Inject

private const val POST_IMAGE_MAX_DIMENSION_PX = 720
private const val POST_IMAGE_MAX_BYTES = 250_000

class CreatePostUseCase @Inject constructor(
    private val imageProcessor: ImageProcessor,
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        authorUid: String,
        authorName: String,
        authorUsername: String,
        authorPhotoBase64: String?,
        authorProvider: AuthProvider,
        title: String,
        text: String,
        linkUrl: String,
        imageUri: Uri?,
        isVerified: Boolean
    ): CreatePostResult {
        val imageBase64 = imageUri?.let { uri ->
            when (
                val processed = imageProcessor.compressToBase64(
                    uri = uri,
                    maxDimensionPx = POST_IMAGE_MAX_DIMENSION_PX,
                    maxBytes = POST_IMAGE_MAX_BYTES
                )
            ) {
                is ImageProcessResult.Success -> processed.base64
                is ImageProcessResult.Error -> return CreatePostResult.Error(processed.message)
            }
        }

        return postRepository.createPost(
            authorUid = authorUid,
            authorName = authorName,
            authorUsername = authorUsername,
            authorPhotoBase64 = authorPhotoBase64,
            authorProvider = authorProvider,
            title = title,
            text = text,
            linkUrl = linkUrl,
            imageBase64 = imageBase64,
            isVerified = isVerified
        )
    }
}