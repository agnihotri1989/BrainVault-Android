package com.kshitiz.brainvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.kshitiz.brainvault.auth.AuthEventBus
import com.kshitiz.brainvault.auth.TokenManager
import com.kshitiz.brainvault.ui.NavGraph
import com.kshitiz.brainvault.ui.Screen
import com.kshitiz.brainvault.ui.theme.BrainVaultTheme
import com.kshitiz.brainvault.auth.AuthViewModel
import com.kshitiz.brainvault.viewmodel.NoteViewModel

class MainActivity : ComponentActivity() {
    private val noteViewModel: NoteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenManager = TokenManager(this)
        // 👇 Key decision — if token exists skip login entirely
        val startDestination = if (tokenManager.isLoggedIn()) Screen.NoteList.route
        else Screen.Login.route
        setContent {
            BrainVaultTheme {
                val navController = rememberNavController()
                // 👇 Listen globally for token expiry / logout events
                LaunchedEffect(Unit) {
                    AuthEventBus.events.collect { event ->
                        when (event) {
                            is AuthEventBus.AuthEvent.TokenExpired,
                            is AuthEventBus.AuthEvent.LoggedOut -> {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }  // clears entire back stack
                                }
                            }
                        }
                    }
                }
                NavGraph(navController = navController, noteViewModel = noteViewModel,authViewModel = authViewModel,
                    startDestination = startDestination)
            }
        }
    }
}



