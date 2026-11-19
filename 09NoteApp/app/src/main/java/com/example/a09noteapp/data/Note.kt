package com.example.a09noteapp.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // ID poznámky, automaticky generované
    val title: String, // Název poznámky
    val content: String, // Obsah poznámky
    @ColumnInfo(defaultValue = "0")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "'General'")
    val category: String = "General"
)
