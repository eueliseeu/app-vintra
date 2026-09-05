package com.vintra.app.domain.model

data class UserProfile(
    val uid: String,
    val name: String,
    val username: String,
    val email: String,
    val birthDateMillis: Long,
    val nationality: String,
    val createdAt: Long,
    val updatedAt: Long
)

private const val NINETY_DAYS_MILLIS = 90L * 24 * 60 * 60 * 1000

sealed interface ProfileEditability {
    data object Editable : ProfileEditability
    data class Locked(val editableAtMillis: Long) : ProfileEditability
}

fun UserProfile?.editability(nowMillis: Long = System.currentTimeMillis()): ProfileEditability {
    if (this == null) return ProfileEditability.Editable
    val editableAt = updatedAt + NINETY_DAYS_MILLIS
    return if (nowMillis >= editableAt) {
        ProfileEditability.Editable
    } else {
        ProfileEditability.Locked(editableAt)
    }
}