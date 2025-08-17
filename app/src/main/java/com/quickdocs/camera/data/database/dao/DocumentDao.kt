package com.quickdocs.camera.data.database.dao

import androidx.room.*
import com.quickdocs.camera.data.database.entities.DocumentEntity
import com.quickdocs.camera.data.database.entities.FolderCount
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Query("SELECT * FROM document_images ORDER BY timestamp DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM document_images WHERE folder_name = :folderName ORDER BY timestamp DESC")
    fun getDocumentsByFolder(folderName: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM document_images WHERE id = :documentId")
    suspend fun getDocumentById(documentId: Long): DocumentEntity?

    @Query("SELECT * FROM document_images WHERE id = :documentId")
    fun getDocumentByIdFlow(documentId: Long): Flow<DocumentEntity?>

    @Query("SELECT DISTINCT folder_name FROM document_images ORDER BY folder_name ASC")
    fun getAllFolders(): Flow<List<String>>

    @Query("SELECT folder_name, COUNT(*) as count FROM document_images GROUP BY folder_name")
    fun getFolderCounts(): Flow<List<FolderCount>>

    @Query("SELECT * FROM document_images WHERE is_favorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT COUNT(*) FROM document_images WHERE folder_name = :folderName")
    suspend fun getDocumentCountInFolder(folderName: String): Int

    @Query("SELECT COUNT(*) FROM document_images")
    suspend fun getTotalDocumentCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(documents: List<DocumentEntity>): List<Long>

    @Update
    suspend fun updateDocument(document: DocumentEntity)

    @Query("UPDATE document_images SET is_favorite = :isFavorite WHERE id = :documentId")
    suspend fun updateFavoriteStatus(documentId: Long, isFavorite: Boolean)

    @Query("UPDATE document_images SET folder_name = :newFolderName WHERE id = :documentId")
    suspend fun moveDocumentToFolder(documentId: Long, newFolderName: String)

    @Delete
    suspend fun deleteDocument(document: DocumentEntity)

    @Query("DELETE FROM document_images WHERE id = :documentId")
    suspend fun deleteDocumentById(documentId: Long)

    @Query("DELETE FROM document_images WHERE folder_name = :folderName")
    suspend fun deleteAllDocumentsInFolder(folderName: String)

    @Query("DELETE FROM document_images WHERE id IN (:documentIds)")
    suspend fun deleteDocumentsByIds(documentIds: List<Long>)

    @Query("SELECT * FROM document_images WHERE file_name LIKE '%' || :query || '%' OR folder_name LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchDocuments(query: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM document_images WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getDocumentsByDateRange(startDate: Long, endDate: Long): Flow<List<DocumentEntity>>
}