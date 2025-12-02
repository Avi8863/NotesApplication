package com.example.notesapplication.repo


import com.example.notesapplication.data.NoteDao
import com.example.notesapplication.data.NoteEntity
import com.example.notesapplication.data.TagConverter
import com.example.notesapplication.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesRepository(private val dao: NoteDao) {

    fun allNotesFlow(): Flow<List<Note>> =
        dao.getAllFlow().map { list -> list.map { it.toNote() } }

    fun searchFlow(query: String): Flow<List<Note>> =
        dao.searchFlow("%${query}%").map { list -> list.map { it.toNote() } }

    suspend fun add(note: Note) {
        dao.insert(note.toEntity())
    }

    suspend fun delete(note: Note) {
        dao.delete(note.toEntity())
    }
}

private fun NoteEntity.toNote() =
    Note(id = id, title = title, body = body, tags = TagConverter.toList(tagsCsv), createdAt = createdAt)

private fun Note.toEntity() =
    NoteEntity(id = id, title = title, body = body, tagsCsv = TagConverter.fromList(tags), createdAt = createdAt)
