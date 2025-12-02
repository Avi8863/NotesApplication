package com.example.notesapplication.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapplication.data.AppDatabase
import com.example.notesapplication.model.Note
import com.example.notesapplication.repo.NotesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).noteDao()
    private val repo = NotesRepository(dao)

    private val _search = MutableStateFlow("")
    val search: StateFlow<String> = _search.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag.asStateFlow()

    val notes: StateFlow<List<Note>> = combine(repo.allNotesFlow(), _search, _selectedTag) { all, q, tag ->
        var filtered = if (q.isBlank()) all else all.filter {
            it.title.contains(q, ignoreCase = true) || it.body.contains(q, ignoreCase = true)
                    || it.tags.any { t -> t.contains(q, ignoreCase = true) }
        }
        tag?.let { t -> filtered = filtered.filter { it.tags.contains(t) } }
        filtered
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setSearch(q: String) { _search.value = q }
    fun setTag(tag: String?) { _selectedTag.value = tag }

    fun add(note: Note) = viewModelScope.launch { repo.add(note) }
    fun delete(note: Note) = viewModelScope.launch { repo.delete(note) }
}
