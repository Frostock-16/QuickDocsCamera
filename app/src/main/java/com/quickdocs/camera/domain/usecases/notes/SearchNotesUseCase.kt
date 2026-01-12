package com.quickdocs.camera.domain.usecases.notes

import com.quickdocs.camera.domain.models.Note
import com.quickdocs.camera.domain.repository.INotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchNotesUseCase @Inject constructor(
    private val repository: INotesRepository
) {
    suspend operator fun invoke(query: String): Flow<List<Note>> {
        return repository.searchNotes(query)
    }
}
