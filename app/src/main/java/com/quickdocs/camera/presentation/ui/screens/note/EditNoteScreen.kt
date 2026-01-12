package com.quickdocs.camera.presentation.ui.screens.note

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quickdocs.camera.domain.models.Note

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    noteId: Long,
    onNavigationBack: () -> Unit,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()
    val existingNote = notes.find { it.id == noteId }

    var title by remember { mutableStateOf(existingNote?.title ?: "") }
    var content by remember { mutableStateOf(existingNote?.content ?: "") }

    LaunchedEffect(existingNote?.id) {
        existingNote?.let {
            title = it.title
            content = it.content
        }
    }

    fun saveAndExit() {
        saveNote(viewModel, existingNote, title, content)
        onNavigationBack()
    }

    BackHandler {
        saveAndExit()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {},
                navigationIcon = {
                    IconButton(onClick = { saveAndExit() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {

                    val isNewNote = existingNote == null

                    // NEW NOTE: Only PIN available
                    if (isNewNote) {
                        IconButton(onClick = {
                            if (title.isNotBlank() || content.isNotBlank()) {
                                viewModel.addNote(
                                    Note(
                                        title = title,
                                        content = content,
                                        isPinned = true
                                    )
                                )
                            }
                            onNavigationBack()
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.PushPin,
                                contentDescription = "Pin"
                            )
                        }
                    }

                    if (!isNewNote) {

                        // Unarchive
                        if (existingNote!!.isArchived) {
                            IconButton(onClick = {
                                viewModel.toggleArchive(existingNote.id)
                                onNavigationBack()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Unarchive,
                                    contentDescription = "Unarchive"
                                )
                            }
                        } else {
                            // Pin
                            IconButton(onClick = {
                                viewModel.togglePin(existingNote.id)
                            }) {
                                Icon(
                                    imageVector = if (existingNote.isPinned)
                                        Icons.Default.PushPin else Icons.Outlined.PushPin,
                                    contentDescription = "Pin"
                                )
                            }

                            // Archive
                            IconButton(onClick = {
                                viewModel.toggleArchive(existingNote.id)
                                onNavigationBack()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Archive,
                                    contentDescription = "Archive"
                                )
                            }
                        }

                        // Delete
                        IconButton(onClick = {
                            viewModel.deleteNote(existingNote)
                            onNavigationBack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                }


            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // Title
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) {
                        Text(
                            text = "Title",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            BasicTextField(
                value = content,
                onValueChange = { content = it },
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.fillMaxSize(),
                decorationBox = { innerTextField ->
                    if (content.isEmpty()) {
                        Text(
                            text = "Note...",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

private fun saveNote(
    viewModel: NotesViewModel,
    existingNote: Note?,
    title: String,
    content: String,
    isPinned: Boolean = false,
    isArchived: Boolean = false
) {
    if (title.isNotBlank() || content.isNotBlank()) {
        if (existingNote != null) {
            viewModel.updateNote(
                existingNote.copy(
                    title = title,
                    content = content
                )
            )
        } else {
            viewModel.addNote(
                Note(
                    title = title,
                    content = content,
                    isPinned = isPinned,
                    isArchived = isArchived
                )
            )
        }
    }
}




@Preview(showBackground = true)
@Composable
fun EditNoteScreenPreview() {
    EditNoteScreen(noteId = -1L, onNavigationBack = {})
}
