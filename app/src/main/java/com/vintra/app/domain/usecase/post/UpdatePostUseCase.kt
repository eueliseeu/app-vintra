package com.vintra.app.domain.usecase.post

import android.net.Uri
import com.vintra.app.domain.repository.PostRepository
import com.vintra.app.domain.repository.UpdatePostResult
import com.vintra.app.domain.service.ImageProcessResult
import com.vintra.app.domain.service.ImageProcessor
import javax.inject.Inject

private const val POST_IMAGE_MAX_DIMENSION_PX = 720
private const val POST_IMAGE_MAX_BYTES = 250_000

class UpdatePostUseCase @Inject constructor(
    private val imageProcessor: ImageProcessor,
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        postId: String,
        authorUid: String,
        title: String,
        text: String,
        linkUrl: String,
        imageUri: Uri?,
        keepExistingImageBase64: String?
    ): UpdatePostResult {
        val imageBase64 = when {
            imageUri != null -> {
                when (
                    val processed = imageProcessor.compressToBase64(
                        uri = imageUri,
                        maxDimensionPx = POST_IMAGE_MAX_DIMENSION_PX,
                        maxBytes = POST_IMAGE_MAX_BYTES
                    )
                ) {
                    is ImageProcessResult.Success -> processed.base64
                    is ImageProcessResult.Error -> return UpdatePostResult.Error(processed.message)
                }
            }
            else -> keepExistingImageBase64
        }

        return postRepository.updatePost(
            postId = postId,
            authorUid = authorUid,
            title = title.trim(),
            text = text.trim(),
            linkUrl = linkUrl.trim(),
            imageBase64 = imageBase64
        )
    }
}