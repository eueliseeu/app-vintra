package com.vintra.app.core.di

import com.vintra.app.data.repository.BalanceRepositoryImpl
import com.vintra.app.domain.repository.BalanceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BalanceModule {

    @Binds
    @Singleton
    abstract fun bindBalanceRepository(impl: BalanceRepositoryImpl): BalanceRepository
}