package com.quickdocs.camera.domain.usecases.notes

import com.quickdocs.camera.domain.repository.INotesRepository
import javax.inject.Inject

class TogglePinUseCase @Inject constructor(
    private val repository: INotesRepository
) {
    suspend operator fun invoke(noteId: Long) {
        repository.togglePin(noteId)
    }
}
