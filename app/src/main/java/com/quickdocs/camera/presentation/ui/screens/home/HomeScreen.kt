package com.quickdocs.camera.presentation.ui.screens.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.quickdocs.camera.data.models.SelectionState
import com.quickdocs.camera.presentation.ui.components.DocumentGrid
import com.quickdocs.camera.presentation.ui.components.FolderCard
import com.quickdocs.camera.presentation.ui.components.FullScreenImageViewer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val galleryState by viewModel.galleryState.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val selectionState by viewModel.selectionState.collectAsState()

    // Handle error messages
    LaunchedEffect(galleryState.errorMessage) {
        galleryState.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    // Handle back button
    BackHandler(enabled = viewMode !is GalleryViewMode.FolderList || selectionState.isSelectionMode) {
        if (selectionState.isSelectionMode) {
            viewModel.exitSelectionMode()
        } else {
            viewModel.navigateBack()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text =when {
                        selectionState.isSelectionMode -> "${selectionState.selectedDocuments.size} selected"
                        viewMode is GalleryViewMode.FolderList -> "Gallery"
                        viewMode is GalleryViewMode.DocumentGrid -> (viewMode as GalleryViewMode.DocumentGrid).folderName
                        viewMode is GalleryViewMode.FullScreenImage -> "Image Viewer"
                        else -> "Gallery"
                    },
                    fontWeight = FontWeight.Medium
                )
            },
            navigationIcon = {
                if (selectionState.isSelectionMode) {
                    IconButton(onClick = { viewModel.exitSelectionMode() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit selection mode"
                        )
                    }
                } else if (viewMode !is GalleryViewMode.FolderList) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Gallery",
                        modifier = Modifier.padding(start = 16.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            actions = {
                when {
                    selectionState.isSelectionMode -> {
                        // Selection mode actions
                        if (galleryState.documents.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    if (selectionState.selectedDocuments.size == galleryState.documents.size) {
                                        viewModel.clearSelection()
                                    } else {
                                        viewModel.selectAllDocuments()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SelectAll,
                                    contentDescription = "Select all",
                                    tint = if (selectionState.selectedDocuments.size == galleryState.documents.size) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }

                        if (selectionState.selectedDocuments.isNotEmpty()) {
                            IconButton(onClick = { viewModel.shareSelectedDocuments() }) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share selected"
                                )
                            }
                            IconButton(onClick = { viewModel.deleteSelectedDocuments() }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete selected"
                                )
                            }
                        }
                    }
                    viewMode is GalleryViewMode.DocumentGrid && galleryState.documents.isNotEmpty() -> {
                        // Show overflow menu only when viewing documents
                        OverflowMenu(
                            onSelectMultipleClick = { viewModel.enterSelectionMode() }
                        )
                    }
                }
            },

            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = if (selectionState.isSelectionMode) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )

        // Main Content
        when (viewMode) {
            is GalleryViewMode.FolderList -> {
                FolderListContent(
                    galleryState = galleryState,
                    onFolderClick = viewModel::openFolder
                )
            }
            is GalleryViewMode.DocumentGrid -> {
                DocumentGridContent(
                    galleryState = galleryState,
                    selectionState = selectionState,
                    onDocumentClick = { documentId ->
                        if (selectionState.isSelectionMode) {
                            viewModel.toggleDocumentSelection(documentId)
                        } else {
                            viewModel.openFullScreenImage(documentId)
                        }
                    },
                    onDocumentLongClick = { documentId ->
                        if (!selectionState.isSelectionMode) {
                            viewModel.enterSelectionMode()
                            viewModel.toggleDocumentSelection(documentId)
                        }
                    }
                )
            }
            is GalleryViewMode.FullScreenImage -> {
                FullScreenContent(
                    documentId = (viewMode as GalleryViewMode.FullScreenImage).documentId,
                    galleryState = galleryState,
                    onBackClick = { viewModel.navigateBack() },
                    onDeleteClick = { document ->
                        viewModel.deleteDocument(document)
                        viewModel.navigateBack()
                    },
                    onShareClick = {document->
                        viewModel.shareDocument(document)
                    }
                )
            }
        }
    }
}

@Composable
private fun FolderListContent(
    galleryState: GalleryState,
    onFolderClick: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (galleryState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (galleryState.folders.isEmpty()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "No folders",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "No folders yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Capture some documents to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Your Documents",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(galleryState.folders) { folder ->
                    FolderCard(
                        folder = folder,
                        onClick = { onFolderClick(folder.name) }
                    )
                }
            }
        }
    }
}


@Composable
private fun OverflowMenu(
    onSelectMultipleClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More options"
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Select multiple") },
            onClick = {
                expanded = false
                onSelectMultipleClick()
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
private fun DocumentGridContent(
    galleryState: GalleryState,
    selectionState: SelectionState,
    onDocumentClick: (Long) -> Unit,
    onDocumentLongClick: (Long) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (galleryState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (galleryState.documents.isEmpty()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "No documents",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "No documents in this folder",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            DocumentGrid(
                documents = galleryState.documents,
                selectionState = selectionState,
                onDocumentClick = { document -> onDocumentClick(document.id) },
                onDocumentLongClick = {document -> onDocumentLongClick(document.id)}
            )
        }
    }
}

@Composable
private fun FullScreenContent(
    documentId: Long,
    galleryState: GalleryState,
    onBackClick: () -> Unit,
    onDeleteClick: (com.quickdocs.camera.data.database.entities.DocumentEntity) -> Unit,
    onShareClick: (com.quickdocs.camera.data.database.entities.DocumentEntity) -> Unit
) {
    val document = galleryState.documents.find { it.id == documentId }
        ?: galleryState.allDocuments.find { it.id == documentId }

    if (document != null) {
        FullScreenImageViewer(
            document = document,
            onBackClick = onBackClick,
            onDeleteClick = { onDeleteClick(document) },
            onShareClick = { onShareClick(document) }
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.BrokenImage,
                    contentDescription = "Document not found",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Document not found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
