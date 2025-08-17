package com.quickdocs.camera.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.quickdocs.camera.data.database.converters.DateConverter
import com.quickdocs.camera.data.database.dao.DocumentDao
import com.quickdocs.camera.data.database.entities.DocumentEntity

@Database(
    entities = [DocumentEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class QuickDocsDatabase : RoomDatabase() {

    abstract fun documentDao(): DocumentDao

    companion object {
        const val DATABASE_NAME = "quickdocs_database"

        @Volatile
        private var INSTANCE: QuickDocsDatabase? = null

        fun getDatabase(context: Context): QuickDocsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuickDocsDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}