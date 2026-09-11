package com.vintra.app.domain.usecase.job

import android.net.Uri
import com.vintra.app.domain.model.JobTag
import com.vintra.app.domain.repository.CreateJobResult
import com.vintra.app.domain.repository.JobRepository
import com.vintra.app.domain.service.ImageProcessResult
import com.vintra.app.domain.service.ImageProcessor
import javax.inject.Inject

private const val JOB_IMAGE_MAX_DIMENSION_PX = 512
private const val JOB_IMAGE_MAX_BYTES = 200_000

class CreateJobUseCase @Inject constructor(
    private val imageProcessor: ImageProcessor,
    private val jobRepository: JobRepository
) {
    suspend operator fun invoke(
        publisherUid: String,
        companyName: String,
        companyUsername: String,
        companyPhotoUri: Uri?,
        companyPhotoBase64Fallback: String?,
        title: String,
        description: String,
        linkUrl: String,
        buttonLabel: String,
        tags: List<JobTag>
    ): CreateJobResult {
        val photoBase64 = when {
            companyPhotoUri != null -> {
                when (
                    val processed = imageProcessor.compressToBase64(
                        uri = companyPhotoUri,
                        maxDimensionPx = JOB_IMAGE_MAX_DIMENSION_PX,
                        maxBytes = JOB_IMAGE_MAX_BYTES
                    )
                ) {
                    is ImageProcessResult.Success -> processed.base64
                    is ImageProcessResult.Error -> return CreateJobResult.Error(processed.message)
                }
            }
            else -> companyPhotoBase64Fallback
        }

        return jobRepository.createJob(
            publisherUid = publisherUid,
            companyName = companyName.trim(),
            companyUsername = companyUsername.trim().removePrefix("@"),
            companyPhotoBase64 = photoBase64,
            title = title.trim(),
            description = description.trim(),
            linkUrl = linkUrl.trim(),
            buttonLabel = buttonLabel.trim().ifBlank { "Open" },
            tags = tags
        )
    }
}