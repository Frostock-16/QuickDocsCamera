package com.quickdocs.camera.domain.models

import com.quickdocs.camera.data.database.entities.NotesEntity
import java.util.Date

data class Note(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val createdAt: Date = Date(),
    val modifiedAt: Date = Date(),
    val isPinned: Boolean = false,
    val isArchived: Boolean = false
)


fun NotesEntity.toDomain() = Note(id, title = title, content=content, createdAt=createdAt, modifiedAt=modifiedAt, isPinned=isPinned, isArchived=isArchived)
fun Note.toEntity() = NotesEntity(id, title=title, content=content, createdAt=createdAt, modifiedAt=modifiedAt, isPinned = isPinned, isArchived = isArchived)