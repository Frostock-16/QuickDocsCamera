package com.quickdocs.camera.data.repository

import com.quickdocs.camera.data.database.dao.DocumentDao
import com.quickdocs.camera.data.database.entities.DocumentEntity
import com.quickdocs.camera.domain.models.DocumentFolder
import com.quickdocs.camera.domain.repository.IDocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepository @Inject constructor(
    private val documentDao: DocumentDao
) : IDocumentRepository {

    override fun getAllDocuments(): Flow<List<DocumentEntity>> {
        return documentDao.getAllDocuments()
    }

    override fun getDocumentsByFolder(folderName: String): Flow<List<DocumentEntity>> {
        return documentDao.getDocumentsByFolder(folderName)
    }

    override suspend fun getDocumentById(documentId: Long): DocumentEntity? {
        return documentDao.getDocumentById(documentId)
    }

    override fun getDocumentByIdFlow(documentId: Long): Flow<DocumentEntity?> {
        return documentDao.getDocumentByIdFlow(documentId)
    }

    override fun getAllFolders(): Flow<List<DocumentFolder>> {
        return documentDao.getAllFolders().map { folderNames ->
            folderNames.map { folderName ->
                val count = documentDao.getDocumentCountInFolder(folderName)
                DocumentFolder(
                    name = folderName,
                    documentCount = count,
                    createdDate = Date()
                )
            }
        }
    }

    override fun getFavoriteDocuments(): Flow<List<DocumentEntity>> {
        return documentDao.getFavoriteDocuments()
    }

    override suspend fun insertDocument(document: DocumentEntity): Long {
        return documentDao.insertDocument(document)
    }

    override suspend fun updateDocument(document: DocumentEntity) {
        documentDao.updateDocument(document.copy(modifiedAt = Date()))
    }

    override suspend fun updateFavoriteStatus(documentId: Long, isFavorite: Boolean) {
        documentDao.updateFavoriteStatus(documentId, isFavorite)
    }

    override suspend fun moveDocumentToFolder(documentId: Long, newFolderName: String) {
        documentDao.moveDocumentToFolder(documentId, newFolderName)
    }

    override suspend fun deleteDocument(document: DocumentEntity) {
        documentDao.deleteDocument(document)
    }

    override suspend fun deleteDocumentById(documentId: Long) {
        documentDao.deleteDocumentById(documentId)
    }

    override suspend fun deleteAllDocumentsInFolder(folderName: String) {
        documentDao.deleteAllDocumentsInFolder(folderName)
    }

    override suspend fun deleteDocumentsByIds(documentIds: List<Long>) {
        documentDao.deleteDocumentsByIds(documentIds)
    }

    override fun searchDocuments(query: String): Flow<List<DocumentEntity>> {
        return documentDao.searchDocuments(query)
    }

    override fun getDocumentsByDateRange(startDate: Long, endDate: Long): Flow<List<DocumentEntity>> {
        return documentDao.getDocumentsByDateRange(startDate, endDate)
    }

    override suspend fun getDocumentCountInFolder(folderName: String): Int {
        return documentDao.getDocumentCountInFolder(folderName)
    }

    override suspend fun getTotalDocumentCount(): Int {
        return documentDao.getTotalDocumentCount()
    }
}