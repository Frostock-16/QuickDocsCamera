package com.quickdocs.camera.domain.repository

import com.quickdocs.camera.domain.models.Note
import kotlinx.coroutines.flow.Flow

interface INotesRepository {

    fun getAllNotes(): Flow<List<Note>>
    suspend fun getNoteById(id:Long):Note?
    suspend fun insertNote(note:Note):Long
    suspend fun deleteNote(note:Note)
    suspend fun updateNote(note:Note)
    suspend fun searchNotes(query:String):Flow<List<Note>>

    suspend fun togglePin(noteId:Long)
    suspend fun toggleArchive(noteId:Long)
}