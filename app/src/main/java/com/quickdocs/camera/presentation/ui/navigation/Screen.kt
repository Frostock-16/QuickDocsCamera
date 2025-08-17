package com.quickdocs.camera.presentation.ui.navigation

import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary

sealed class Screen(val route: String) {
    object Camera : Screen("camera")
    object Gallery : Screen("gallery")
    object DocumentDetail : Screen("document_detail/{documentId}") {
        fun createRoute(documentId: Long) = "document_detail/$documentId"
    }
    object Settings : Screen("settings")
}

// Bottom navigation destinations
sealed class BottomNavItem(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Camera : BottomNavItem(
        route = Screen.Camera.route,
        title = "Camera",
        icon = androidx.compose.material.icons.Icons.Default.CameraAlt
    )
    object Gallery : BottomNavItem(
        route = Screen.Gallery.route,
        title = "Gallery",
        icon = androidx.compose.material.icons.Icons.Default.PhotoLibrary
    )
}

val bottomNavigationItems = listOf(
    BottomNavItem.Camera,
    BottomNavItem.Gallery
)