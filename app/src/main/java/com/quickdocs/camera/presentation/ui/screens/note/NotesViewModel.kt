package com.quickdocs.camera.presentation.ui.screens.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickdocs.camera.domain.models.Note
import javax.inject.Inject
import com.quickdocs.camera.domain.usecases.notes.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val insertNoteUseCase: InsertNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val searchNotesUseCase: SearchNotesUseCase,
    private val togglePinUseCase: TogglePinUseCase,
    private val toggleArchiveUseCase: ToggleArchiveUseCase
): ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init{
        observerNotes()
    }

    fun loadNoteById(id: Long, onLoaded: ((Note?) -> Unit)? = null) {
        viewModelScope.launch {
            val currentNotes = notes.value
            val note = currentNotes.find { it.id == id }
            onLoaded?.invoke(note)
        }
    }


    private fun observerNotes() {
        getNotesUseCase().onEach { notes ->
            _notes.value = notes
        }.launchIn(viewModelScope)
    }

    fun addNote(note:Note)
    {
        viewModelScope.launch {
            insertNoteUseCase(note)
        }
    }

    fun updateNote(note:Note)
    {
        viewModelScope.launch {
            updateNoteUseCase(note)
        }
    }

    fun deleteNote(note:Note)
    {
        viewModelScope.launch {
            deleteNoteUseCase(note)
        }
    }

    fun togglePin(noteId:Long) {
        viewModelScope.launch {
            togglePinUseCase(noteId)
        }
    }

    fun toggleArchive(noteId:Long) {
        viewModelScope.launch {
            toggleArchiveUseCase(noteId)
        }
    }

    suspend fun searchNotes(query:String) {
        _searchQuery.value = query
        searchNotesUseCase(query).onEach { result ->
            _notes.value = result
        }.launchIn(viewModelScope)
    }
}