package com.vintra.app.domain.usecase.profile

import com.vintra.app.domain.repository.GetPhotoResult
import com.vintra.app.domain.repository.PhotoRepository
import javax.inject.Inject

class GetProfilePhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(uid: String): GetPhotoResult = photoRepository.getPhoto(uid)
}