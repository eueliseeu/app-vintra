package com.vintra.app.domain.repository

import kotlinx.coroutines.flow.Flow

sealed interface GetBalanceResult {
    data class Success(val amountCents: Long) : GetBalanceResult
    data class Error(val message: String) : GetBalanceResult
}

sealed interface InitBalanceResult {
    data object Success : InitBalanceResult
    data class Error(val message: String) : InitBalanceResult
}

interface BalanceRepository {
    fun observeBalance(uid: String): Flow<GetBalanceResult>
    suspend fun initBalance(uid: String): InitBalanceResult
}