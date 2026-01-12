package com.quickdocs.camera.data.database.dao;

import androidx.room.Dao;
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.quickdocs.camera.data.database.entities.NotesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM notes ORDER BY created_at DESC")
    fun getAllNotes(): Flow<List<NotesEntity>>

    @Query("SELECT * FROM notes WHERE id= :id")
    suspend fun getNoteById(id: Long): NotesEntity?

    @Query("""
        SELECT * FROM notes
        WHERE title LIKE '%' || :query || '%' 
           OR content LIKE '%' || :query || '%'
        ORDER BY created_at DESC
    """)
    fun searchNotes(query: String): Flow<List<NotesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NotesEntity): Long

    @Delete
    suspend fun deleteNote(note: NotesEntity)

    @Update
    suspend fun updateNote(note: NotesEntity)

    @Query("UPDATE notes SET is_pinned = NOT is_pinned WHERE id = :noteId")
    suspend fun togglePin(noteId: Long)

    @Query("UPDATE notes SET is_archived = NOT is_archived WHERE id = :noteId")
    suspend fun toggleArchive(noteId: Long)

}
