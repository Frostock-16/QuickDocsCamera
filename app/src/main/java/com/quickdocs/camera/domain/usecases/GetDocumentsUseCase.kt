package com.quickdocs.camera.domain.usecases

import com.quickdocs.camera.data.database.entities.DocumentEntity
import com.quickdocs.camera.domain.repository.IDocumentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDocumentsUseCase @Inject constructor(
    private val repository: IDocumentRepository
) {
    fun getAllDocuments(): Flow<List<DocumentEntity>> {
        return repository.getAllDocuments()
    }

    fun getDocumentsByFolder(folderName: String): Flow<List<DocumentEntity>> {
        return repository.getDocumentsByFolder(folderName)
    }

    suspend fun getDocumentById(documentId: Long): DocumentEntity? {
        return repository.getDocumentById(documentId)
    }

    fun searchDocuments(query: String): Flow<List<DocumentEntity>> {
        return repository.searchDocuments(query)
    }
}