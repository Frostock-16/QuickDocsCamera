package com.quickdocs.camera.presentation.ui.screens.camera

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickdocs.camera.data.models.CameraState
import com.quickdocs.camera.data.models.DocumentFolder
import com.quickdocs.camera.domain.usecases.GetFoldersUseCase
import com.quickdocs.camera.domain.usecases.SaveDocumentUseCase
import com.quickdocs.camera.presentation.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val saveDocumentUseCase: SaveDocumentUseCase,
    private val getFoldersUseCase: GetFoldersUseCase
) : ViewModel() {

    private val _cameraState = MutableStateFlow(CameraState())
    val cameraState: StateFlow<CameraState> = _cameraState.asStateFlow()

    private var imageCapture: ImageCapture? = null
    private val outputDirectory: File by lazy { getOutputDirectory() }

    init {
        loadAvailableFolders()
    }

    fun setImageCapture(imageCapture: ImageCapture) {
        this.imageCapture = imageCapture
        _cameraState.value = _cameraState.value.copy(isPreviewReady = true)
    }

    fun updatePermissionStatus(hasPermission: Boolean) {
        _cameraState.value = _cameraState.value.copy(hasPermission = hasPermission)
    }

    fun capturePhoto() {
        val imageCapture = imageCapture ?: return

        _cameraState.value = _cameraState.value.copy(isCapturing = true)

        val name = SimpleDateFormat(Constants.FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis()) + Constants.IMAGE_EXTENSION

        val tempFile = File(outputDirectory, "temp_$name")

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()

        imageCapture.takePicture(
            outputFileOptions,
            androidx.core.content.ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    _cameraState.value = _cameraState.value.copy(
                        isCapturing = false,
                        errorMessage = "Photo capture failed: ${exception.message}"
                    )
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    _cameraState.value = _cameraState.value.copy(
                        isCapturing = false,
                        lastCapturedImage = tempFile.absolutePath,
                        isBottomSheetVisible = true
                    )
                }
            }
        )
    }

    fun saveToFolder(folderName: String) {
        val tempFilePath = _cameraState.value.lastCapturedImage ?: return
        val tempFile = File(tempFilePath)

        if (!tempFile.exists()) {
            _cameraState.value = _cameraState.value.copy(
                errorMessage = "Temporary file not found"
            )
            return
        }

        viewModelScope.launch {
            try {
                val folderDir = File(outputDirectory, folderName)
                if (!folderDir.exists()) {
                    folderDir.mkdirs()
                }

                val finalFileName = SimpleDateFormat(Constants.FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis()) + Constants.IMAGE_EXTENSION
                val finalFile = File(folderDir, finalFileName)

                // Move temp file to final location
                val success = tempFile.renameTo(finalFile)

                if (success) {
                    val result = saveDocumentUseCase(
                        filePath = finalFile.absolutePath,
                        folderName = folderName,
                        fileName = finalFileName
                    )

                    if (result.isSuccess) {
                        // Update folders list
                        loadAvailableFolders()

                        _cameraState.value = _cameraState.value.copy(
                            lastCapturedImage = null,
                            isBottomSheetVisible = false,
                            errorMessage = null
                        )
                    } else {
                        _cameraState.value = _cameraState.value.copy(
                            errorMessage = "Failed to save to database: ${result.exceptionOrNull()?.message}"
                        )
                    }
                } else {
                    _cameraState.value = _cameraState.value.copy(
                        errorMessage = "Failed to save image file"
                    )
                }
            } catch (e: Exception) {
                _cameraState.value = _cameraState.value.copy(
                    errorMessage = "Error saving image: ${e.message}"
                )
            }
        }
    }

    fun createNewFolderAndSave(folderName: String) {
        saveToFolder(folderName)
    }

    fun hideBottomSheet() {
        // Clean up temp file if user cancels
        _cameraState.value.lastCapturedImage?.let { tempPath ->
            val tempFile = File(tempPath)
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }

        _cameraState.value = _cameraState.value.copy(
            lastCapturedImage = null,
            isBottomSheetVisible = false
        )
    }

    fun clearError() {
        _cameraState.value = _cameraState.value.copy(errorMessage = null)
    }

    private fun loadAvailableFolders() {
        viewModelScope.launch {
            try {
                getFoldersUseCase().collect { folders ->
                    val allFolders = mutableListOf<DocumentFolder>()

                    val hasDefaultFolder = folders.any { it.name == Constants.DEFAULT_FOLDER_NAME }
                    if (!hasDefaultFolder) {
                        allFolders.add(DocumentFolder(Constants.DEFAULT_FOLDER_NAME, 0))
                    }

                    allFolders.addAll(folders)

                    _cameraState.value = _cameraState.value.copy(
                        availableFolders = allFolders.sortedBy {
                            if (it.name == Constants.DEFAULT_FOLDER_NAME) 0 else 1
                        }
                    )
                }
            } catch (e: Exception) {
                _cameraState.value = _cameraState.value.copy(
                    errorMessage = "Failed to load folders: ${e.message}"
                )
            }
        }
    }

    internal fun getOutputDirectory(): File {
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, "QuickDocs").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir
    }
}
