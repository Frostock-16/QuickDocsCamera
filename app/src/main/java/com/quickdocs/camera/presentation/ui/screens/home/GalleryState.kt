package com.quickdocs.camera.presentation.ui.screens.home

import com.quickdocs.camera.data.database.entities.DocumentEntity
import com.quickdocs.camera.data.models.DocumentFolder

data class GalleryState(
    val isLoading: Boolean = false,
    val folders: List<DocumentFolder> = emptyList(),
    val currentFolder: String? = null,
    val documents: List<DocumentEntity> = emptyList(),
    val allDocuments: List<DocumentEntity> = emptyList(),
    val selectedDocumentId: Long? = null,
    val isFullScreenMode: Boolean = false,
    val errorMessage: String? = null,
    val totalDocumentCount: Int = 0,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false
)

sealed class GalleryViewMode {
    object FolderList : GalleryViewMode()
    data class DocumentGrid(val folderName: String) : GalleryViewMode()
    data class FullScreenImage(val documentId: Long) : GalleryViewMode()
}