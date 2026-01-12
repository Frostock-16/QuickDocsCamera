package com.quickdocs.camera.di

import android.content.Context
import com.quickdocs.camera.data.database.QuickDocsDatabase
import com.quickdocs.camera.data.database.dao.DocumentDao
import com.quickdocs.camera.data.database.dao.NotesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideQuickDocsDatabase(
        @ApplicationContext context: Context
    ): QuickDocsDatabase {
        return QuickDocsDatabase.getDatabase(context)
    }

    @Provides
    fun provideDocumentDao(database: QuickDocsDatabase): DocumentDao =
        database.documentDao()

    @Provides
    fun provideNotesDao(database: QuickDocsDatabase): NotesDao =
        database.notesDao()
}
