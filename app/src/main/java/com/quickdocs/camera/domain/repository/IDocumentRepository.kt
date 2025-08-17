package com.quickdocs.camera.domain.repository

import com.quickdocs.camera.data.database.entities.DocumentEntity
import com.quickdocs.camera.data.models.DocumentFolder
import kotlinx.coroutines.flow.Flow

interface IDocumentRepository {
    fun getAllDocuments(): Flow<List<DocumentEntity>>
    fun getDocumentsByFolder(folderName: String): Flow<List<DocumentEntity>>
    suspend fun getDocumentById(documentId: Long): DocumentEntity?
    fun getDocumentByIdFlow(documentId: Long): Flow<DocumentEntity?>
    fun getAllFolders(): Flow<List<DocumentFolder>>
    fun getFavoriteDocuments(): Flow<List<DocumentEntity>>
    suspend fun insertDocument(document: DocumentEntity): Long
    suspend fun updateDocument(document: DocumentEntity)
    suspend fun updateFavoriteStatus(documentId: Long, isFavorite: Boolean)
    suspend fun moveDocumentToFolder(documentId: Long, newFolderName: String)
    suspend fun deleteDocument(document: DocumentEntity)
    suspend fun deleteDocumentById(documentId: Long)
    suspend fun deleteAllDocumentsInFolder(folderName: String)
    suspend fun deleteDocumentsByIds(documentIds: List<Long>)
    fun searchDocuments(query: String): Flow<List<DocumentEntity>>
    fun getDocumentsByDateRange(startDate: Long, endDate: Long): Flow<List<DocumentEntity>>
    suspend fun getDocumentCountInFolder(folderName: String): Int
    suspend fun getTotalDocumentCount(): Int
}