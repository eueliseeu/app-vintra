package com.vintra.app.core.di

import com.vintra.app.data.repository.JobRepositoryImpl
import com.vintra.app.domain.repository.JobRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class JobModule {

    @Binds
    @Singleton
    abstract fun bindJobRepository(impl: JobRepositoryImpl): JobRepository
}