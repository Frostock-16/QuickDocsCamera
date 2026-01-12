package com.quickdocs.camera.presentation.ui.navigation

sealed class Screen(val route: String) {
    object Camera : Screen("camera")
    object Gallery : Screen("gallery")
    object Note:Screen("note")
    object EditNote:Screen("edit_note_screen?noteId={noteId}"){
        fun passNoteId(noteId:Long):String{
            return "edit_note_screen?noteId=$noteId"
        }
    }
    object Archive:Screen("archive")
    object DocumentDetail : Screen("document_detail/{documentId}")
    object Settings : Screen("settings")
}

