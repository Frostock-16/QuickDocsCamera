package com.quickdocs.camera.domain.usecases

import com.quickdocs.camera.data.database.entities.DocumentEntity
import com.quickdocs.camera.domain.repository.IDocumentRepository
import java.io.File
import javax.inject.Inject

class DeleteDocumentUseCase @Inject constructor(
    private val repository: IDocumentRepository
) {
    suspend operator fun invoke(document: DocumentEntity): Result<Unit> {
        return try {
            // Delete the physical file
            val file = File(document.filePath)
            if (file.exists()) {
                file.delete()
            }

            // Delete thumbnail if exists
            document.thumbnailPath?.let { thumbnailPath ->
                val thumbnailFile = File(thumbnailPath)
                if (thumbnailFile.exists()) {
                    thumbnailFile.delete()
                }
            }

            // Delete from database
            repository.deleteDocument(document)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteById(documentId: Long): Result<Unit> {
        return try {
            val document = repository.getDocumentById(documentId)
            if (document != null) {
                invoke(document)
            } else {
                Result.failure(Exception("Document not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMultiple(documentIds: List<Long>): Result<Unit> {
        return try {
            documentIds.forEach { documentId ->
                val document = repository.getDocumentById(documentId)
                if (document != null) {
                    // Delete physical file
                    val file = File(document.filePath)
                    if (file.exists()) {
                        file.delete()
                    }

                    // Delete thumbnail if exists
                    document.thumbnailPath?.let { thumbnailPath ->
                        val thumbnailFile = File(thumbnailPath)
                        if (thumbnailFile.exists()) {
                            thumbnailFile.delete()
                        }
                    }
                }
            }

            // Delete from database in batch
            repository.deleteDocumentsByIds(documentIds)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}