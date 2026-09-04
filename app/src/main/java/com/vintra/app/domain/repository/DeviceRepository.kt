package com.vintra.app.domain.repository

sealed interface DeviceRegistrationResult {
    data object Success : DeviceRegistrationResult
    data object AlreadyRegistered : DeviceRegistrationResult
    data class Error(val message: String) : DeviceRegistrationResult
}

interface DeviceRepository {
    suspend fun registerDevice(uid: String): DeviceRegistrationResult
}