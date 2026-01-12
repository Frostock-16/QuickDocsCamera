package com.quickdocs.camera.di

import com.quickdocs.camera.data.repository.NotesRepository
import com.quickdocs.camera.domain.usecases.notes.DeleteNoteUseCase
import com.quickdocs.camera.domain.usecases.notes.GetNotesUseCase
import com.quickdocs.camera.domain.usecases.notes.InsertNoteUseCase
import com.quickdocs.camera.domain.usecases.notes.ToggleArchiveUseCase
import com.quickdocs.camera.domain.usecases.notes.TogglePinUseCase
import com.quickdocs.camera.domain.usecases.notes.UpdateNoteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetNotesUseCase(repository: NotesRepository): GetNotesUseCase {
        return GetNotesUseCase(repository)
    }

    @Provides
    fun provideInsertNoteUseCase(repository: NotesRepository): InsertNoteUseCase {
        return InsertNoteUseCase(repository)
    }

    @Provides
    fun provideUpdateNoteUseCase(repository: NotesRepository): UpdateNoteUseCase {
        return UpdateNoteUseCase(repository)
    }

    @Provides
    fun provideDeleteNoteUseCase(repository: NotesRepository): DeleteNoteUseCase {
        return DeleteNoteUseCase(repository)
    }

    @Provides
    fun provideTogglePinUseCase(repository: NotesRepository): TogglePinUseCase {
        return TogglePinUseCase(repository)
    }

    @Provides
    fun provideToggleArchiveUseCase(repository: NotesRepository): ToggleArchiveUseCase {
        return ToggleArchiveUseCase(repository)
    }
}