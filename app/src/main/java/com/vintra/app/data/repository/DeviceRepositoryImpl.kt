package com.vintra.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.vintra.app.core.device.DeviceIdProvider
import com.vintra.app.domain.repository.DeviceRegistrationResult
import com.vintra.app.domain.repository.DeviceRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val COLLECTION_DEVICES = "devices"

class DeviceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val deviceIdProvider: DeviceIdProvider
) : DeviceRepository {

    override suspend fun registerDevice(uid: String): DeviceRegistrationResult {
        val deviceId = deviceIdProvider.getDeviceId()
        val data = mapOf(
            "uid" to uid,
            "createdAt" to System.currentTimeMillis()
        )
        return try {
            firestore.collection(COLLECTION_DEVICES).document(deviceId).set(data).await()
            DeviceRegistrationResult.Success
        } catch (exception: FirebaseFirestoreException) {
            if (exception.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                DeviceRegistrationResult.AlreadyRegistered
            } else {
                DeviceRegistrationResult.Error(exception.message ?: "Erro ao registrar dispositivo.")
            }
        } catch (exception: Exception) {
            DeviceRegistrationResult.Error(exception.message ?: "Erro ao registrar dispositivo.")
        }
    }
}