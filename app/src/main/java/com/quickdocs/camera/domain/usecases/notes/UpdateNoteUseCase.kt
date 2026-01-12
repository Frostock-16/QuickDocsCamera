package com.quickdocs.camera.domain.usecases.notes

import com.quickdocs.camera.domain.models.Note
import com.quickdocs.camera.domain.repository.INotesRepository
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
    private val repository: INotesRepository
) {
    suspend operator fun invoke(note: Note) {
        repository.updateNote(note)
    }
}
