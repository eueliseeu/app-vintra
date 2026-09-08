package com.vintra.app.domain.usecase.balance

import com.vintra.app.domain.repository.BalanceRepository
import com.vintra.app.domain.repository.GetBalanceResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBalanceUseCase @Inject constructor(
    private val balanceRepository: BalanceRepository
) {
    operator fun invoke(uid: String): Flow<GetBalanceResult> = balanceRepository.observeBalance(uid)
}