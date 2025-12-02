package com.example.notesapplication.di

import android.content.Context
import com.example.notesapplication.data.AppDatabase
import com.example.notesapplication.repo.NotesRepository

object AppModule {
    fun provideDatabase(context: Context) = AppDatabase.getInstance(context)
    fun provideRepository(context: Context) = NotesRepository(provideDatabase(context).noteDao())
}
