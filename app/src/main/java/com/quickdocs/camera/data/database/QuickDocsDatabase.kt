package com.quickdocs.camera.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration
import com.quickdocs.camera.data.database.converters.DateConverter
import com.quickdocs.camera.data.database.dao.DocumentDao
import com.quickdocs.camera.data.database.dao.NotesDao
import com.quickdocs.camera.data.database.entities.DocumentEntity
import com.quickdocs.camera.data.database.entities.NotesEntity

@Database(
    entities = [DocumentEntity::class, NotesEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class QuickDocsDatabase : RoomDatabase() {

    abstract fun documentDao(): DocumentDao
    abstract fun notesDao(): NotesDao

    companion object {
        const val DATABASE_NAME = "quickdocs_database"

        // v1 -> v2 migration to add Notes table
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS notes (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                modified_at INTEGER NOT NULL,
                is_pinned INTEGER NOT NULL DEFAULT 0,
                is_archived INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
                )
            }
        }


        @Volatile
        private var INSTANCE: QuickDocsDatabase? = null

        fun getDatabase(context: Context): QuickDocsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuickDocsDatabase::class.java,
                    DATABASE_NAME
                )
                    // IMPORTANT: no fallbackToDestructiveMigration here,
                    // or it will nuke your data again.
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
