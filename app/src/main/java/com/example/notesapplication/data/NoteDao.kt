package com.example.notesapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: NoteEntity): Long

    @Delete
    suspend fun delete(entity: NoteEntity)

    @Query("SELECT * FROM notes WHERE title LIKE :q OR body LIKE :q OR tagsCsv LIKE :q ORDER BY createdAt DESC")
    fun searchFlow(q: String): Flow<List<NoteEntity>>
}
