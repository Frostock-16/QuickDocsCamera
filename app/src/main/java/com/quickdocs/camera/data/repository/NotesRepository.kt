package com.quickdocs.camera.data.repository

import com.quickdocs.camera.data.database.dao.NotesDao
import com.quickdocs.camera.domain.models.Note
import com.quickdocs.camera.domain.models.toDomain
import com.quickdocs.camera.domain.models.toEntity
import com.quickdocs.camera.domain.repository.INotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val notesDao: NotesDao
) : INotesRepository {
    override fun getAllNotes(): Flow<List<Note>> =
        notesDao.getAllNotes()
            .map { notes->
                notes
//                    .filter{it.isArchived.not()}
                    .sortedByDescending{it.isPinned}
                    .map{it.toDomain()}
            }

    override suspend fun getNoteById(id: Long): Note? {
        return notesDao.getNoteById(id)?.toDomain()
    }

    override suspend fun insertNote(note: Note): Long {
        return notesDao.insertNote(note.toEntity())
    }

    override suspend fun deleteNote(note: Note) {
        return notesDao.deleteNote(note.toEntity())
    }

    override suspend fun updateNote(note: Note) {
        return notesDao.updateNote(note.toEntity())
    }

    override suspend fun searchNotes(query: String): Flow<List<Note>> {
        return notesDao.searchNotes(query).map {
            list->
            list.filter{it.isArchived.not()}.map{it.toDomain()}
        }
    }

    override suspend fun togglePin(noteId: Long) {
        return notesDao.togglePin(noteId)
    }

    override suspend fun toggleArchive(noteId: Long) {
        return notesDao.toggleArchive(noteId)
    }

}