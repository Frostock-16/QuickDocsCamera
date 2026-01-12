package com.quickdocs.camera.presentation.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.NoteAlt
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.quickdocs.camera.presentation.ui.screens.camera.CameraScreen
import com.quickdocs.camera.presentation.ui.screens.camera.CameraViewModel
import com.quickdocs.camera.presentation.ui.screens.home.HomeScreen
import com.quickdocs.camera.presentation.ui.screens.home.HomeViewModel
import com.quickdocs.camera.presentation.ui.screens.note.ArchiveScreen
import com.quickdocs.camera.presentation.ui.screens.note.EditNoteScreen
import com.quickdocs.camera.presentation.ui.screens.note.NoteScreen
import com.quickdocs.camera.presentation.ui.screens.note.NotesViewModel

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
                startDestination = Screen.Camera.route,
                modifier = Modifier,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it / 15 },
                        animationSpec = tween(200)
                    ) + fadeIn(animationSpec = tween(200))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(180)) + scaleOut(
                        targetScale = 0.97f,
                        animationSpec = tween(180)
                    )
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(200))
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(150))
                }
            ) {
                // Camera Screen
                composable(Screen.Camera.route) {
                    val viewModel: CameraViewModel = hiltViewModel()
                    CameraScreen(
                        viewModel = viewModel
                    )
                }

                // Gallery Screen
                composable(Screen.Gallery.route) {
                    val viewModel: HomeViewModel = hiltViewModel()
                    HomeScreen(
                        viewModel = viewModel
                    )
                }

                //Note Screen
                composable(Screen.Note.route){
                    val viewModel: NotesViewModel = hiltViewModel()
                    val notes = viewModel.notes.collectAsState().value
                    NoteScreen(
                        notes = notes,
                        onNoteClick = {noteId ->
                            navController.navigate(Screen.EditNote.passNoteId(noteId))
                        },
                        onAddNoteClick = {
                            navController.navigate(Screen.EditNote.route)
                        },
                        onArchiveClick = {
                            navController.navigate(Screen.Archive.route)
                        }
                    )
                }

                //Archive Screen
                composable(Screen.Archive.route){
                    val viewModel: NotesViewModel = hiltViewModel()
                    val notes = viewModel.notes.collectAsState().value
                    ArchiveScreen(
                        notes = notes.filter { it.isArchived },
                        onNoteClick = {noteId ->
                            navController.navigate(Screen.EditNote.passNoteId(noteId))
                        },
                        onNavigationBack = { navController.popBackStack()}
                    )
                }

                composable(Screen.EditNote.route,
                    arguments = listOf(
                        navArgument("noteId"){
                            type = NavType.LongType
                            defaultValue = -1L
                        }
                    )
                )
                {
                    backStackEntry ->
                    val noteId = backStackEntry.arguments?.getLong("noteId") ?: -1L
                    EditNoteScreen(
                        noteId = noteId,
                        onNavigationBack = { navController.popBackStack()},
                        viewModel = hiltViewModel()
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
            ),
            NavigationItem(
                title="Note",
                icon=Icons.Default.NoteAlt,
                route=Screen.Note.route
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
