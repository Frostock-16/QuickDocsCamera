package com.quickdocs.camera.presentation.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.quickdocs.camera.data.database.entities.DocumentEntity
import java.io.File

class ShareHelper(private val context: Context) {


    //Share a single document
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

            // Start the share chooser
            val chooserIntent = Intent.createChooser(intent, "Share Documents")
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(chooserIntent)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Convert file path to URI using FileProvider
    private fun getFileUri(filePath: String): Uri? {
        return try {
            val file = File(filePath)
            if (file.exists()) {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
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