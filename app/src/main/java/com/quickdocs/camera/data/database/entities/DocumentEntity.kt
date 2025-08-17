package com.quickdocs.camera.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

@Entity(tableName = "document_images")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "folder_name")
    val folderName: String,

    @ColumnInfo(name = "file_path")
    val filePath: String,

    @ColumnInfo(name = "file_name")
    val fileName: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Date,

    @ColumnInfo(name = "file_size")
    val fileSize: Long = 0,

    @ColumnInfo(name = "thumbnail_path")
    val thumbnailPath: String? = null,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),

    @ColumnInfo(name = "modified_at")
    val modifiedAt: Date = Date()
)

data class FolderCount(
    @ColumnInfo(name = "folder_name") val folderName: String,
    @ColumnInfo(name = "count") val count: Int
)
