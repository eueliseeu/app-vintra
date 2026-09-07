package com.vintra.app.data.model

data class UserPhotoDto @JvmOverloads constructor(
    val base64: String = "",
    val updatedAt: Long = 0L
)