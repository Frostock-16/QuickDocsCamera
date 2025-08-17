package com.quickdocs.camera.di

import com.quickdocs.camera.data.repository.DocumentRepository
import com.quickdocs.camera.domain.repository.IDocumentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDocumentRepository(
        documentRepository: DocumentRepository
    ): IDocumentRepository
}