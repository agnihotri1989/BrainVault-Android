package com.kshitiz.brainvault.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kshitiz.brainvault.data.Note
import com.kshitiz.brainvault.ui.Screen
import com.kshitiz.brainvault.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(navController: NavController, viewModel: NoteViewModel) {
    val notes by viewModel.allNotes.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("🧠 BrainVault") }) },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AskAI.route) },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) { Icon(Icons.Default.Search, contentDescription = "Ask AI") }

                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddNote.route) }
                ) { Icon(Icons.Default.Add, contentDescription = "Add Note") }
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(notes) { note ->
                NoteCard(note = note, onDelete = { viewModel.deleteNote(note) })
            }
        }
    }
}

@Composable
fun NoteCard(note: Note, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(note.title, style = MaterialTheme.typography.titleMedium)
                Text(note.content, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                Text(
                    if (note.isSynced) "✅ Synced" else "⏳ Pending sync",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (note.isSynced) Color.Green else Color.Gray
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}