package com.vintra.app.domain.usecase.profile

import android.net.Uri
import com.vintra.app.domain.repository.PhotoRepository
import com.vintra.app.domain.repository.SavePhotoResult
import com.vintra.app.domain.service.ImageProcessResult
import com.vintra.app.domain.service.ImageProcessor
import javax.inject.Inject

sealed interface UploadPhotoResult {
    data object Success : UploadPhotoResult
    data class Error(val message: String) : UploadPhotoResult
}

class UploadProfilePhotoUseCase @Inject constructor(
    private val imageProcessor: ImageProcessor,
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(uid: String, uri: Uri): UploadPhotoResult {
        return when (val processed = imageProcessor.compressToBase64(uri)) {
            is ImageProcessResult.Error -> UploadPhotoResult.Error(processed.message)
            is ImageProcessResult.Success -> {
                when (val saved = photoRepository.savePhoto(uid, processed.base64)) {
                    is SavePhotoResult.Success -> UploadPhotoResult.Success
                    is SavePhotoResult.Error -> UploadPhotoResult.Error(saved.message)
                }
            }
        }
    }
}