package com.quickdocs.camera.presentation.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.quickdocs.camera.presentation.ui.screens.camera.CameraScreen
import com.quickdocs.camera.presentation.ui.screens.camera.CameraViewModel
import com.quickdocs.camera.presentation.ui.screens.home.HomeScreen
import com.quickdocs.camera.presentation.ui.screens.home.HomeViewModel

@Composable
fun QuickDocsNavigation(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Camera.route
            ) {
                // Camera Screen
                composable(Screen.Camera.route) {
                    val viewModel: CameraViewModel = hiltViewModel()
                    CameraScreen(
                        viewModel = viewModel,
                        navController = navController
                    )
                }

                // Gallery Screen
                composable(Screen.Gallery.route) {
                    val viewModel: HomeViewModel = hiltViewModel()
                    HomeScreen(
                        viewModel = viewModel,
                        navController = navController
                    )
                }

                // Document Detail Screen
                composable(Screen.DocumentDetail.route) { backStackEntry ->
                    val documentId = backStackEntry.arguments?.getString("documentId")?.toLongOrNull() ?: 0L
                    // TODO: Implement DocumentDetailScreen
                    // DocumentDetailScreen(documentId = documentId, navController = navController)
                }

                // Settings Screen
                composable(Screen.Settings.route) {
                    // TODO: Implement SettingsScreen
                    // SettingsScreen(navController = navController)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val items = listOf(
            NavigationItem(
                title = "Camera",
                icon = Icons.Default.CameraAlt,
                route = Screen.Camera.route
            ),
            NavigationItem(
                title = "Gallery",
                icon = Icons.Default.PhotoLibrary,
                route = Screen.Gallery.route
            )
        )

        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

private data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)
