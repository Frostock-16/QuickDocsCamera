package com.quickdocs.camera.domain.usecases

import com.quickdocs.camera.domain.models.DocumentFolder
import com.quickdocs.camera.domain.repository.IDocumentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFoldersUseCase @Inject constructor(
    private val repository: IDocumentRepository
) {
    operator fun invoke(): Flow<List<DocumentFolder>> {
        return repository.getAllFolders()
    }

}