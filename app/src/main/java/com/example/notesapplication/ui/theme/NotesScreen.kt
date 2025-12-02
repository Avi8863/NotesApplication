package com.example.notesapplication.ui.theme

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notesapplication.model.Note
import com.example.notesapplication.vm.NotesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotesScreen(vm: NotesViewModel) {

    val notes by vm.notes.collectAsState()
    val search by vm.search.collectAsState()
    val selectedTag by vm.selectedTag.collectAsState()

    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAdd by remember { mutableStateOf(false) }

    val allTags = remember(notes) { notes.flatMap { it.tags }.distinct() }

    // -----------------------------------------
    // SHOW BOTTOM SHEET ONLY WHEN NEEDED
    // -----------------------------------------
    if (showAdd) {
        ModalBottomSheet(
            onDismissRequest = {
                showAdd = false
            },
            sheetState = sheetState
        ) {
            AddNoteBottomSheet { title, body, tagsCsv ->
                val tags = tagsCsv.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                vm.add(Note(title = title, body = body, tags = tags))

                scope.launch { sheetState.hide() }

                showAdd = false
            }
        }
    }

    // -----------------------------------------
    // MAIN SCREEN UI
    // -----------------------------------------
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            stickyHeader {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = 12.dp)
                ) {
                    Text("Notes Manager", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Organize your notes with tags and search",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = search,
                        onValueChange = { vm.setSearch(it) },
                        placeholder = { Text("Search notes by title, body, or tags...") },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Filter by tags:", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))

                    val scroll = rememberScrollState()

                    Row(
                        modifier = Modifier
                            .horizontalScroll(scroll)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // All chip
                        FilterChipView(
                            text = "all",
                            selected = selectedTag == null
                        ) { vm.setTag(null) }

                        allTags.forEach { tag ->
                            FilterChipView(
                                text = tag,
                                selected = selectedTag == tag
                            ) {
                                vm.setTag(if (selectedTag == tag) null else tag)
                            }
                        }
                    }
                }
            }

            // Notes list
            items(notes) { note ->
                NoteCard(
                    note = note,
                    onDelete = { vm.delete(note) }
                )
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}
