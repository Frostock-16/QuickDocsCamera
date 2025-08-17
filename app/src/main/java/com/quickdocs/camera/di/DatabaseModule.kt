package com.quickdocs.camera.di

import android.content.Context
import androidx.room.Room
import com.quickdocs.camera.data.database.QuickDocsDatabase
import com.quickdocs.camera.data.database.dao.DocumentDao
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
    fun provideQuickDocsDatabase(@ApplicationContext context: Context): QuickDocsDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            QuickDocsDatabase::class.java,
            QuickDocsDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideDocumentDao(database: QuickDocsDatabase): DocumentDao {
        return database.documentDao()
    }
}