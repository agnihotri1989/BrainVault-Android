package com.kshitiz.brainvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.kshitiz.brainvault.ui.NavGraph
import com.kshitiz.brainvault.ui.theme.BrainVaultTheme
import com.kshitiz.brainvault.viewmodel.NoteViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BrainVaultTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController, viewModel = viewModel)
            }
        }
    }
}



