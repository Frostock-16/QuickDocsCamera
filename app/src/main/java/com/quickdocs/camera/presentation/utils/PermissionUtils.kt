package com.quickdocs.camera.presentation.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtils {
    const val CAMERA_PERMISSION = Manifest.permission.CAMERA

    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    val requiredPermissions = arrayOf(CAMERA_PERMISSION)
}