package com.vintra.app.data.mapper

import com.vintra.app.data.model.UserProfileDto
import com.vintra.app.domain.model.AuthProvider
import com.vintra.app.domain.model.UserProfile

fun UserProfileDto.toDomain(uid: String): UserProfile = UserProfile(
    uid = uid,
    name = name,
    username = username,
    email = email,
    birthDateMillis = birthDateMillis,
    nationality = nationality,
    provider = runCatching { AuthProvider.valueOf(provider) }.getOrDefault(AuthProvider.UNKNOWN),
    createdAt = createdAt,
    updatedAt = updatedAt,
    isVerified = isVerified
)

fun UserProfile.toDto(): UserProfileDto = UserProfileDto(
    name = name,
    username = username,
    email = email,
    birthDateMillis = birthDateMillis,
    nationality = nationality,
    provider = provider.name,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isVerified = isVerified
)