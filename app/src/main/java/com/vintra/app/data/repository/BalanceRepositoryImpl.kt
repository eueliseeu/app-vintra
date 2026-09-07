package com.vintra.app.data.repository

import com.vintra.app.domain.model.BalanceDto

import com.google.firebase.firestore.FirebaseFirestore
import com.vintra.app.data.mapper.toDomain
import com.vintra.app.domain.repository.BalanceRepository
import com.vintra.app.domain.repository.GetBalanceResult
import com.vintra.app.domain.repository.InitBalanceResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val COLLECTION_BALANCES = "balances"

class BalanceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BalanceRepository {

    override suspend fun getBalance(uid: String): GetBalanceResult = try {
        val snapshot = firestore.collection(COLLECTION_BALANCES).document(uid).get().await()
        val dto = snapshot.toObject(BalanceDto::class.java)
        GetBalanceResult.Success(dto?.toDomain(uid)?.amountCents ?: 0L)
    } catch (exception: Exception) {
        GetBalanceResult.Error(exception.message ?: "Erro ao buscar saldo.")
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