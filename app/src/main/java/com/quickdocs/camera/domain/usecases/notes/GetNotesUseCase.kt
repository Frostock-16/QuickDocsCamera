package com.quickdocs.camera.domain.usecases.notes

import com.quickdocs.camera.domain.models.Note
import com.quickdocs.camera.domain.repository.INotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
    private val repository: INotesRepository
) {
    operator fun invoke(): Flow<List<Note>>{
        return repository.getAllNotes()
    }
}