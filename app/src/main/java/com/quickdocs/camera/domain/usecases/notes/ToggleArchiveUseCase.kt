package com.quickdocs.camera.domain.usecases.notes

import com.quickdocs.camera.domain.repository.INotesRepository
import javax.inject.Inject

class ToggleArchiveUseCase @Inject constructor(
    private val repository: INotesRepository
) {
    suspend operator fun invoke(noteId: Long) {
        repository.toggleArchive(noteId)
    }
}
