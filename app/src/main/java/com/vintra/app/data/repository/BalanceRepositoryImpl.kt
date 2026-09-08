package com.vintra.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.vintra.app.data.mapper.toDomain
import com.vintra.app.data.model.BalanceDto
import com.vintra.app.domain.repository.BalanceRepository
import com.vintra.app.domain.repository.GetBalanceResult
import com.vintra.app.domain.repository.InitBalanceResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val COLLECTION_BALANCES = "balances"

class BalanceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BalanceRepository {

    override fun observeBalance(uid: String): Flow<GetBalanceResult> = callbackFlow {
        val registration = firestore.collection(COLLECTION_BALANCES).document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(GetBalanceResult.Error(error.message ?: "Erro ao observar saldo."))
                    return@addSnapshotListener
                }

                val dto = snapshot?.toObject(BalanceDto::class.java)
                trySend(GetBalanceResult.Success(dto?.toDomain(uid)?.amountCents ?: 0L))
            }

        awaitClose { registration.remove() }
    }

    override suspend fun initBalance(uid: String): InitBalanceResult = try {
        val docRef = firestore.collection(COLLECTION_BALANCES).document(uid)
        val snapshot = docRef.get().await()
        if (!snapshot.exists()) {
            docRef.set(BalanceDto(amountCents = 0L, updatedAt = System.currentTimeMillis())).await()
        }
        InitBalanceResult.Success
    } catch (exception: Exception) {
        InitBalanceResult.Error(exception.message ?: "Erro ao inicializar saldo.")
    }
}