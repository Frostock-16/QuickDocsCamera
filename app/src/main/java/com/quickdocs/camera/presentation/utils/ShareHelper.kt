package com.quickdocs.camera.presentation.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.quickdocs.camera.data.database.entities.DocumentEntity
import com.quickdocs.camera.presentation.ui.screens.home.GalleryState
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

class ShareHelper(private val context: Context) {

    private val _galleryState = MutableStateFlow(GalleryState())


    // Share a single document
    fun shareDocument(document: DocumentEntity) {
        shareDocuments(listOf(document))
    }

    // Share multiple documents
    fun shareDocuments(documents: List<DocumentEntity>) {
        try {
            val uris = documents.mapNotNull { document ->
                getFileUri(document.filePath)
            }

            if (uris.isEmpty()) {
                return
            }

            val intent = if (uris.size == 1) {
                // Single file sharing
                createSingleShareIntent(uris.first())
            } else {
                // Multiple files sharing
                createMultipleShareIntent(uris)
            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            val chooserIntent = Intent.createChooser(intent, "Share Documents")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            // Verify that there's at least one app that can handle the intent
            if (chooserIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(chooserIntent)
            } else {
                _galleryState.value = _galleryState.value.copy(
                    errorMessage = "No app found to share documents"
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Convert file path to URI using FileProvider
    private fun getFileUri(filePath: String): Uri? {
        return try {
            val file = File(filePath)

            if (file.exists() && file.canRead()) {
                val authority = "${context.packageName}.fileprovider"

                val uri = FileProvider.getUriForFile(context, authority, file)
                return uri
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Create intent for sharing a single file
    private fun createSingleShareIntent(uri: Uri): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    // Create intent for sharing multiple files
    private fun createMultipleShareIntent(uris: List<Uri>): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "image/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}