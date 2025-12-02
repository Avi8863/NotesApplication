package com.example.notesapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.notesapplication.ui.theme.NavGraph
import com.example.notesapplication.ui.theme.NotesTheme
import com.example.notesapplication.vm.NotesViewModel

class MainActivity : ComponentActivity() {

    private lateinit var vm: NotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = NotesViewModel(application) // AndroidViewModel with Application param

        setContent {
            NotesTheme {
                NavGraph(vm = vm)
            }
        }
    }
}
