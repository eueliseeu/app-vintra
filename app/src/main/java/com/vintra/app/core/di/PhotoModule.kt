package com.vintra.app.core.di

import com.vintra.app.data.image.AndroidImageProcessor
import com.vintra.app.data.repository.PhotoRepositoryImpl
import com.vintra.app.domain.repository.PhotoRepository
import com.vintra.app.domain.service.ImageProcessor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PhotoModule {

    @Binds
    @Singleton
    abstract fun bindPhotoRepository(impl: PhotoRepositoryImpl): PhotoRepository

    @Binds
    @Singleton
    abstract fun bindImageProcessor(impl: AndroidImageProcessor): ImageProcessor
}