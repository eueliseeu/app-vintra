package com.vintra.app.data.model

import com.google.firebase.firestore.PropertyName

data class UserProfileDto @JvmOverloads constructor(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val birthDateMillis: Long = 0L,
    val nationality: String = "",
    val provider: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    @get:PropertyName("isVerified")
    @set:PropertyName("isVerified")
    var isVerified: Boolean = false
)