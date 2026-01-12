package com.quickdocs.camera.domain.models

data class SelectionState(
    val isSelectionMode:Boolean = false,
    val selectedDocuments:Set<Long> = emptySet()
)