package com.vintra.app.domain.usecase.profile

import com.vintra.app.domain.repository.GetProfileResult
import com.vintra.app.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(uid: String): GetProfileResult = profileRepository.getProfile(uid)
}