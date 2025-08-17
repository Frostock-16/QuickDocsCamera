package com.quickdocs.camera.presentation.ui.screens.camera

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.quickdocs.camera.presentation.ui.components.CameraPreview
import com.quickdocs.camera.presentation.ui.components.CaptureButton
import com.quickdocs.camera.presentation.ui.components.FolderSelectionBottomSheet
import com.quickdocs.camera.presentation.ui.components.PermissionDeniedContent
import com.quickdocs.camera.presentation.utils.PermissionUtils

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val cameraState by viewModel.cameraState.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState()

    val cameraPermissionState = rememberPermissionState(
        PermissionUtils.CAMERA_PERMISSION
    ) { isGranted ->
        viewModel.updatePermissionStatus(isGranted)
    }

    // Handle error messages
    LaunchedEffect(cameraState.errorMessage) {
        cameraState.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    // Check initial permission status
    LaunchedEffect(Unit) {
        viewModel.updatePermissionStatus(cameraPermissionState.status.isGranted)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            cameraPermissionState.status.isGranted -> {
                // Camera content
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onPreviewReady = viewModel::setImageCapture
                )

                // Capture button overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    CaptureButton(
                        onClick = viewModel::capturePhoto,
                        isCapturing = cameraState.isCapturing
                    )
                }
            }
            cameraPermissionState.status.shouldShowRationale -> {
                PermissionDeniedContent(
                    text = "Camera permission is required to capture documents. Please grant the permission.",
                    onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
                )
            }
            else -> {
                PermissionDeniedContent(
                    text = "Camera permission is required to use this app.",
                    onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
                )
            }
        }

        // Bottom sheet for folder selection
        if (cameraState.isBottomSheetVisible) {
            FolderSelectionBottomSheet(
                sheetState = bottomSheetState,
                availableFolders = cameraState.availableFolders,
                onDismiss = viewModel::hideBottomSheet,
                onFolderSelected = viewModel::saveToFolder,
                onCreateNewFolder = viewModel::createNewFolderAndSave
            )
        }
    }
}