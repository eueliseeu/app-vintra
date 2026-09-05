package com.vintra.app.data.mapper

import com.vintra.app.data.model.UserProfileDto
import com.vintra.app.domain.model.UserProfile

fun UserProfileDto.toDomain(uid: String): UserProfile = UserProfile(
    uid = uid,
    name = name,
    username = username,
    email = email,
    birthDateMillis = birthDateMillis,
    nationality = nationality,
    createdAt = createdAt,
    updatedAt = updatedAt
)