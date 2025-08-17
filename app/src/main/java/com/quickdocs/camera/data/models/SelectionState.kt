package com.quickdocs.camera.data.models

data class SelectionState(
    val isSelectionMode:Boolean = false,
    val selectedDocuments:Set<Long> = emptySet()
)