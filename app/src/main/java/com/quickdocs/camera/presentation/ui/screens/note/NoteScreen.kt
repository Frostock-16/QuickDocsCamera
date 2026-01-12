package com.quickdocs.camera.presentation.ui.screens.note

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quickdocs.camera.domain.models.Note
import com.quickdocs.camera.presentation.ui.components.NoteCard

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    notes: List<Note>,
    onNoteClick: (Long) -> Unit,
    onAddNoteClick: () -> Unit,
    onArchiveClick:()->Unit
) {
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    // Filter logic
    val filteredNotes = notes.filter {
        val q = searchQuery.text.trim().lowercase()
        q.isEmpty() || it.title.lowercase().contains(q) || it.content.lowercase().contains(q)
    }

    val pinnedNotes = filteredNotes.filter { it.isPinned && !it.isArchived }
    val otherNotes = filteredNotes.filter { !it.isPinned && !it.isArchived}

    Scaffold(
        topBar = {
            if (!isSearching) {
                TopAppBar(
                    title = {},
                    actions = {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search"
                            )
                        }
                        IconButton(onClick=onArchiveClick){
                            Icon(
                                imageVector = Icons.Filled.Archive,
                                contentDescription = "Archive"
                            )
                        }
                    }
                )
            } else {
                SearchTopBar(
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onClose = {
                        isSearching = false
                        searchQuery = TextFieldValue("")
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNoteClick,
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "New note"
                )
            }
        }
    ) { padding ->
        if (notes.isEmpty()) {
            EmptyStateUI(padding)
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .padding(padding)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp
            ) {
                if (pinnedNotes.isNotEmpty()) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        SectionHeader("Pinned")
                    }

                    items(
                        pinnedNotes,
                        key = { it.id }
                    ) { note ->
                        NoteCard(
                            note = note,
                            onClick = { onNoteClick(note.id) },
                            modifier = Modifier
                        )
                    }
                }

                if (otherNotes.isNotEmpty()) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        SectionHeader("Others")
                    }

                    items(
                        otherNotes,
                        key = { it.id }
                    ) { note ->
                        NoteCard(
                            note = note,
                            onClick = { onNoteClick(note.id) },
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
    )
}

@Composable
private fun SearchTopBar(
    searchQuery: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onClose: () -> Unit
) {
    TextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        placeholder = { Text("Search notes") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = {
                if (searchQuery.text.isNotEmpty()) onQueryChange(TextFieldValue(""))
                else onClose()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Close Search"
                )
            }
        }
    )
}

@Composable
private fun EmptyStateUI(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No notes yet",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Tap the edit button to create one.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NotesScreenPreview() {
    NoteScreen(
        notes = listOf(
            Note(title = "Preview Note", content = "This is a test note"),
            Note(title = "Another Note", content = "More content here"),
        ),
        onNoteClick = {},
        onAddNoteClick = {},
        onArchiveClick = {}
    )
}
