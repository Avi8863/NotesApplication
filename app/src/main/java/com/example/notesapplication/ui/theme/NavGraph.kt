package com.example.notesapplication.ui.theme

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.notesapplication.vm.NotesViewModel

@Composable
fun NavGraph(vm: NotesViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            NotesScreen(vm = vm)
        }
    }
}
