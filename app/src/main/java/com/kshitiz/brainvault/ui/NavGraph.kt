package com.kshitiz.brainvault.ui

import AddNoteScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kshitiz.brainvault.viewmodel.NoteViewModel
import com.kshitiz.brainvault.ui.screens.NoteListScreen
import com.kshitiz.brainvault.ui.screens.AskAIScreen
import com.kshitiz.brainvault.ui.screens.LoginScreen
import com.kshitiz.brainvault.ui.screens.RegisterScreen
import com.kshitiz.brainvault.auth.AuthViewModel

sealed class Screen(val route: String) {
    object Login    : Screen("login")
    object Register : Screen("register")
    object NoteList : Screen("note_list")
    object AddNote  : Screen("add_note")
    object AskAI    : Screen("ask_ai")
}

@Composable
fun NavGraph(navController: NavHostController,
             noteViewModel: NoteViewModel,
             authViewModel: AuthViewModel,        // 👈 new
             startDestination: String
) {
    NavHost(navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController, authViewModel)
        }
        composable(Screen.NoteList.route) {
            NoteListScreen(navController, noteViewModel,authViewModel)
        }
        composable(Screen.AddNote.route) {
            AddNoteScreen(navController, noteViewModel)
        }
        composable(Screen.AskAI.route) {
            AskAIScreen(navController, noteViewModel)
        }
    }
}