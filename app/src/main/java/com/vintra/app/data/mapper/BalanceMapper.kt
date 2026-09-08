package com.vintra.app.data.mapper

import com.vintra.app.domain.model.Balance
import com.vintra.app.data.model.BalanceDto

fun BalanceDto.toDomain(uid: String): Balance = Balance(
    uid = uid,
    amountCents = amountCents,
    updatedAt = updatedAt
)

fun Balance.toDto(): BalanceDto = BalanceDto(
    amountCents = amountCents,
    updatedAt = updatedAt
)