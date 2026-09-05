package com.vintra.app.data.model

data class UserProfileDto @JvmOverloads constructor(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val birthDateMillis: Long = 0L,
    val nationality: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)