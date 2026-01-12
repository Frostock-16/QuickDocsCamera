package com.quickdocs.camera.domain.models

data class CameraState(
    val isPreviewReady: Boolean = false,
    val isCapturing: Boolean = false,
    val hasPermission: Boolean = false,
    val errorMessage: String? = null,
    val lastCapturedImage: String? = null,
    val availableFolders: List<DocumentFolder> = emptyList(),
    val isBottomSheetVisible: Boolean = false
)