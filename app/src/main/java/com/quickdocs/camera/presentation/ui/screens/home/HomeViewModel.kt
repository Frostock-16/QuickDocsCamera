package com.quickdocs.camera.presentation.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickdocs.camera.data.database.entities.DocumentEntity
import com.quickdocs.camera.data.models.DocumentFolder
import com.quickdocs.camera.data.models.SelectionState
import com.quickdocs.camera.domain.usecases.DeleteDocumentUseCase
import com.quickdocs.camera.domain.usecases.GetDocumentsUseCase
import com.quickdocs.camera.domain.usecases.GetFoldersUseCase
import com.quickdocs.camera.presentation.utils.Constants
import com.quickdocs.camera.presentation.utils.ShareHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDocumentsUseCase: GetDocumentsUseCase,
    private val getFoldersUseCase: GetFoldersUseCase,
    private val deleteDocumentUseCase: DeleteDocumentUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val shareHelper = ShareHelper(context)

    private val _selectionState = MutableStateFlow(SelectionState())
    val selectionState: StateFlow<SelectionState> = _selectionState.asStateFlow()

    private val _galleryState = MutableStateFlow(GalleryState())
    val galleryState: StateFlow<GalleryState> = _galleryState.asStateFlow()

    private val _viewMode = MutableStateFlow<GalleryViewMode>(GalleryViewMode.FolderList)
    val viewMode: StateFlow<GalleryViewMode> = _viewMode.asStateFlow()

    fun enterSelectionMode() {
        _selectionState.value = _selectionState.value.copy(isSelectionMode = true)
    }
    fun exitSelectionMode(){
        _selectionState.value = _selectionState.value.copy(isSelectionMode = false)
    }

    fun selectAllDocuments(){
        val allDocumentIds = _galleryState.value.documents.map { it.id }.toSet()
        _selectionState.value = _selectionState.value.copy(selectedDocuments = allDocumentIds)
    }

    fun clearSelection()
    {
        _selectionState.value = _selectionState.value.copy(selectedDocuments = emptySet())
    }


    fun deleteSelectedDocuments()
    {
        viewModelScope.launch {
            try {
                val selectedIds = _selectionState.value.selectedDocuments
                val documentsToDelete = _galleryState.value.documents.filter { selectedIds.contains(it.id) }

                documentsToDelete.forEach { document->
                    deleteDocumentUseCase(document)
                }

                when(val currentMode = _viewMode.value){
                    is GalleryViewMode.DocumentGrid -> {
                        loadDocumentsForFolder(currentMode.folderName)
                    }
                    else -> {
                        loadInitialData()
                    }
                }

                exitSelectionMode()
                _galleryState.value = _galleryState.value.copy(
                    errorMessage = "Deleted ${documentsToDelete.size} document(s)"
                )
            }catch (e: Exception){
                _galleryState.value = _galleryState.value.copy(
                    errorMessage = "Error deleting documents: ${e.message}"
                )
            }
        }
    }

    fun shareSelectedDocuments() {
        viewModelScope.launch {
            try {
                val selectedIds = _selectionState.value.selectedDocuments
                val documentsToShare = _galleryState.value.documents.filter {
                    selectedIds.contains(it.id)
                }

                if (documentsToShare.isNotEmpty()) {
                    shareHelper.shareDocuments(documentsToShare)

                    _galleryState.value = _galleryState.value.copy(
                        errorMessage = "Sharing ${documentsToShare.size} document(s)..."
                    )

                    exitSelectionMode()
                } else {
                    _galleryState.value = _galleryState.value.copy(
                        errorMessage = "No documents selected to share"
                    )
                }

            } catch (e: Exception) {
                _galleryState.value = _galleryState.value.copy(
                    errorMessage = "Failed to share documents: ${e.message}"
                )
            }
        }
    }

    fun shareDocument(document: DocumentEntity) {
        try {
            shareHelper.shareDocument(document)
        } catch (e: Exception) {
            _galleryState.value = _galleryState.value.copy(
                errorMessage = "Failed to share document: ${e.message}"
            )
        }
    }

    fun toggleDocumentSelection(documentId: Long)
    {
        val currentSelection = _selectionState.value.selectedDocuments
        val newSelection = if(currentSelection.contains(documentId)){
            currentSelection - documentId
        }else{
            currentSelection + documentId
        }
        _selectionState.value = _selectionState.value.copy(selectedDocuments = newSelection)
    }



    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _galleryState.value = _galleryState.value.copy(isLoading = true)

            try {
                // Combine folders and all documents data
                combine(
                    getFoldersUseCase(),
                    getDocumentsUseCase.getAllDocuments()
                ) { folders, allDocuments ->
                    Pair(folders, allDocuments)
                }.catch { e ->
                    _galleryState.value = _galleryState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load data: ${e.message}"
                    )
                }.collect { (folders, allDocuments) ->
                    val allFolders = buildList {
                        add(DocumentFolder(
                            name = Constants.DEFAULT_FOLDER_NAME,
                            documentCount = allDocuments.size,
                            createdDate = Date()
                        ))
                        addAll(folders.filter { it.name != Constants.DEFAULT_FOLDER_NAME })
                    }.sortedWith(compareBy<DocumentFolder> {
                        it.name != Constants.DEFAULT_FOLDER_NAME
                    }.thenBy { it.name })

                    _galleryState.value = _galleryState.value.copy(
                        isLoading = false,
                        folders = allFolders,
                        allDocuments = allDocuments,
                        totalDocumentCount = allDocuments.size,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _galleryState.value = _galleryState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load initial data: ${e.message}"
                )
            }
        }
    }

    fun openFolder(folderName: String) {
        _viewMode.value = GalleryViewMode.DocumentGrid(folderName)
        loadDocumentsForFolder(folderName)
    }

    private fun loadDocumentsForFolder(folderName: String) {
        viewModelScope.launch {
            _galleryState.value = _galleryState.value.copy(
                isLoading = true,
                currentFolder = folderName
            )

            try {
                val documentsFlow = if (folderName == Constants.DEFAULT_FOLDER_NAME) {
                    getDocumentsUseCase.getAllDocuments()
                } else {
                    getDocumentsUseCase.getDocumentsByFolder(folderName)
                }

                documentsFlow.catch { e ->
                    _galleryState.value = _galleryState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load documents: ${e.message}"
                    )
                }.collect { documents ->
                    _galleryState.value = _galleryState.value.copy(
                        isLoading = false,
                        documents = documents,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _galleryState.value = _galleryState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load folder documents: ${e.message}"
                )
            }
        }
    }

    fun openFullScreenImage(documentId: Long) {
        _viewMode.value = GalleryViewMode.FullScreenImage(documentId)
        _galleryState.value = _galleryState.value.copy(selectedDocumentId = documentId)
    }

    fun navigateBack() {
        when (_viewMode.value) {
            is GalleryViewMode.FullScreenImage -> {
                val currentFolder = _galleryState.value.currentFolder ?: Constants.DEFAULT_FOLDER_NAME
                _viewMode.value = GalleryViewMode.DocumentGrid(currentFolder)
                _galleryState.value = _galleryState.value.copy(selectedDocumentId = null)
            }
            is GalleryViewMode.DocumentGrid -> {
                _viewMode.value = GalleryViewMode.FolderList
                _galleryState.value = _galleryState.value.copy(
                    currentFolder = null,
                    documents = emptyList(),
                    selectedDocumentId = null
                )
            }
            else -> {
                // Already at folder list, do nothing or handle app exit
            }
        }
    }

    fun deleteDocument(document: DocumentEntity) {
        viewModelScope.launch {
            try {
                val result = deleteDocumentUseCase(document)
                if (result.isSuccess) {
                    when (_viewMode.value) {
                        is GalleryViewMode.DocumentGrid -> {
                            _galleryState.value.currentFolder?.let { folder ->
                                loadDocumentsForFolder(folder)
                            }
                        }
                        else -> {
                            loadInitialData()
                        }
                    }
                } else {
                    _galleryState.value = _galleryState.value.copy(
                        errorMessage = "Failed to delete document: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _galleryState.value = _galleryState.value.copy(
                    errorMessage = "Error deleting document: ${e.message}"
                )
            }
        }
    }

    fun toggleFavorite(documentId: Long) {
        viewModelScope.launch {
            try {
                _galleryState.value = _galleryState.value.copy(
                    errorMessage = "Favorite functionality coming soon"
                )
            } catch (e: Exception) {
                _galleryState.value = _galleryState.value.copy(
                    errorMessage = "Error toggling favorite: ${e.message}"
                )
            }
        }
    }

    fun searchDocuments(query: String) {
        _galleryState.value = _galleryState.value.copy(
            searchQuery = query,
            isSearchActive = query.isNotBlank()
        )

        if (query.isNotBlank()) {
            viewModelScope.launch {
                try {
                    getDocumentsUseCase.searchDocuments(query).collect { searchResults ->
                        _galleryState.value = _galleryState.value.copy(documents = searchResults)
                    }
                } catch (e: Exception) {
                    _galleryState.value = _galleryState.value.copy(
                        errorMessage = "Search failed: ${e.message}"
                    )
                }
            }
        } else {
            _galleryState.value.currentFolder?.let { folder ->
                loadDocumentsForFolder(folder)
            }
        }
    }

    fun clearSearch() {
        _galleryState.value = _galleryState.value.copy(
            searchQuery = "",
            isSearchActive = false
        )
        _galleryState.value.currentFolder?.let { folder ->
            loadDocumentsForFolder(folder)
        }
    }

    fun clearError() {
        _galleryState.value = _galleryState.value.copy(errorMessage = null)
    }
}