package com.quickdocs.camera.di

import com.quickdocs.camera.data.repository.DocumentRepository
import com.quickdocs.camera.data.repository.NotesRepository
import com.quickdocs.camera.domain.repository.IDocumentRepository
import com.quickdocs.camera.domain.repository.INotesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindDocumentRepository(
        documentRepositoryImpl: DocumentRepository
    ): IDocumentRepository

    @Binds
    abstract fun bindNotesRepository(
        documentRepositoryImpl: NotesRepository
    ): INotesRepository
}
