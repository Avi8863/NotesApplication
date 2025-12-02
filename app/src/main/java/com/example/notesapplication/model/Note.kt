package com.example.notesapplication.model

data class Note(
    val id: Long = 0,
    val title: String,
    val body: String,
    val tags: List<String>,
    val createdAt: Long = System.currentTimeMillis()
)
