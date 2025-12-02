package com.example.notesapplication.data

object TagConverter {
    fun fromList(tags: List<String>): String = tags.joinToString(",") { it.trim() }
    fun toList(csv: String): List<String> =
        csv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
