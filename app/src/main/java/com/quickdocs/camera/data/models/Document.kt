package com.quickdocs.camera.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Document(
    val id: Long = 0,
    val fileName: String,
    val filePath: String,
    val folderName: String,
    val createdDate: Date,
    val fileSize: Long,
    val thumbnailPath: String? = null
) : Parcelable

@Parcelize
data class DocumentFolder(
    val name: String,
    val documentCount: Int = 0,
    val createdDate: Date = Date()
) : Parcelable