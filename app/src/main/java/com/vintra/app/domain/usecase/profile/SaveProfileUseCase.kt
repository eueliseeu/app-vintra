package com.vintra.app.domain.usecase.profile

import com.vintra.app.domain.repository.ProfileRepository
import com.vintra.app.domain.repository.SaveProfileResult
import javax.inject.Inject

class SaveProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(
        uid: String,
        name: String,
        username: String,
        email: String,
        birthDateMillis: Long,
        nationality: String,
        previousUsername: String?
    ): SaveProfileResult = profileRepository.saveProfile(
        uid = uid,
        name = name,
        username = username,
        email = email,
        birthDateMillis = birthDateMillis,
        nationality = nationality,
        previousUsername = previousUsername
    )
}