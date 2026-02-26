package com.kshitiz.brainvault.ui

import AddNoteScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kshitiz.brainvault.viewmodel.NoteViewModel
import com.kshitiz.brainvault.ui.screens.NoteListScreen
import com.kshitiz.brainvault.ui.screens.AskAIScreen
sealed class Screen(val route: String) {
    object NoteList : Screen("note_list")
    object AddNote  : Screen("add_note")
    object AskAI    : Screen("ask_ai")
}

@Composable
fun NavGraph(navController: NavHostController, viewModel: NoteViewModel) {
    NavHost(navController, startDestination = Screen.NoteList.route) {
        composable(Screen.NoteList.route) {
            NoteListScreen(navController, viewModel)
        }
        composable(Screen.AddNote.route) {
            AddNoteScreen(navController, viewModel)
        }
        composable(Screen.AskAI.route) {
            AskAIScreen(navController, viewModel)
        }
    }
}