package com.vintra.app.domain.usecase.profile

import com.vintra.app.domain.repository.ProfileRepository
import com.vintra.app.domain.repository.UsernameAvailability
import javax.inject.Inject

class CheckUsernameAvailabilityUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(username: String, uid: String): UsernameAvailability =
        profileRepository.isUsernameAvailable(username, uid)
}