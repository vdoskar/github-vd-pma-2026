package com.example.a09noteapp.data

import android.content.Context
import androidx.room3.Room
import androidx.room3.migration.Migration
import androidx.sqlite.execSQL

object NoteHubDatabaseInstance {

    /*@Volatile znamená, že proměnná je viditelná pro všechna vlákna okamžitě. */
    /* V praxi: když se v jednom vlákně změní hodnota (např. INSTANCE databáze),
       ostatní vlákna uvidí tu novou hodnotu hned — ne až po zpoždění z cache paměti. */
    @Volatile
    private var INSTANCE: NoteHubDatabase? = null

    private val MIGRATION_1_2 = Migration(1, 2) { connection ->
        connection.execSQL("ALTER TABLE note_table ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
        connection.execSQL("ALTER TABLE note_table ADD COLUMN category TEXT NOT NULL DEFAULT 'General'")
    }

    /**
     * Vrátí instanci databáze. Pokud ještě neexistuje, vytvoří ji.
     */
    fun getDatabase(context: Context): NoteHubDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE?.let { return@synchronized it }

            val instance = Room.databaseBuilder(
                context.applicationContext,
                NoteHubDatabase::class.java,
                "notehub_database"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
            INSTANCE = instance
            instance
        }
    }

}
