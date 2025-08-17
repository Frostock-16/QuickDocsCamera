package com.quickdocs.camera.domain.usecases

import com.quickdocs.camera.data.database.entities.DocumentEntity
import com.quickdocs.camera.domain.repository.IDocumentRepository
import java.io.File
import java.util.Date
import javax.inject.Inject

class SaveDocumentUseCase @Inject constructor(
    private val repository: IDocumentRepository
) {
    suspend operator fun invoke(
        filePath: String,
        folderName: String,
        fileName: String? = null
    ): Result<Long> {
        return try {
            val file = File(filePath)
            if (!file.exists()) {
                return Result.failure(Exception("File does not exist: $filePath"))
            }

            val finalFileName = fileName ?: file.name
            val document = DocumentEntity(
                folderName = folderName,
                filePath = filePath,
                fileName = finalFileName,
                timestamp = Date(),
                fileSize = file.length(),
                createdAt = Date(),
                modifiedAt = Date()
            )

            val documentId = repository.insertDocument(document)
            Result.success(documentId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}