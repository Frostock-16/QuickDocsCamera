package com.quickdocs.camera.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity (tableName = "notes")
data class NotesEntity(
    @PrimaryKey(autoGenerate=true)
    val id:Long=0,

    @ColumnInfo(name="title")
    val title:String = "",

    @ColumnInfo(name="content")
    val content:String,

//    @ColumnInfo(name="timestamp")
//    val timestamp: Date,

    @ColumnInfo(name="created_at")
    val createdAt:Date = Date(),

    @ColumnInfo(name="modified_at")
    val modifiedAt:Date = Date(),

    @ColumnInfo(name = "is_pinned")
    val isPinned: Boolean = false,

    @ColumnInfo(name = "is_archived")
    val isArchived: Boolean = false

)