package com.vintra.app.data.mapper

import com.vintra.app.data.model.UserPhotoDto
import com.vintra.app.domain.model.UserPhoto

fun UserPhotoDto.toDomain(uid: String): UserPhoto = UserPhoto(
    uid = uid,
    base64 = base64,
    updatedAt = updatedAt
)

fun UserPhoto.toDto(): UserPhotoDto = UserPhotoDto(
    base64 = base64,
    updatedAt = updatedAt
)