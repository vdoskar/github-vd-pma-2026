package com.example.a09noteapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a09noteapp.data.Note
import com.example.a09noteapp.data.NoteHubDatabaseInstance
import com.example.a09noteapp.databinding.ActivityAddNoteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.Date

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private var createdAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createdAt = savedInstanceState?.getLong(STATE_CREATED_AT) ?: System.currentTimeMillis()
        binding.spinnerCategory.showNoteCategories()
        val formattedDate = DateFormat.getDateTimeInstance().format(Date(createdAt))
        binding.textCreatedAtInfo.text = getString(R.string.note_created_at, formattedDate)

        val db = NoteHubDatabaseInstance.getDatabase(this)
        val noteDao = db.noteDao()

        binding.btnSaveNote.setOnClickListener {
            val title = binding.editTextTitle.text.toString()
            val content = binding.editTextContent.text.toString()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, R.string.note_fields_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val note = Note(
                title = title,
                content = content,
                createdAt = createdAt,
                category = binding.spinnerCategory.selectedItem.toString()
            )

            // Spustí korutinu na vlákně určeném pro vstupně/výstupní operace (IO),
            // které jsou vhodné pro práci s databází, aby se nezablokovalo UI.
            CoroutineScope(Dispatchers.IO).launch {
                // Uložení poznámky do databáze na vlákně pro IO operace (nezablokuje UI)
                noteDao.insert(note)

                // Po uložení zavře tuto aktivitu a vrátí se zpět na hlavní obrazovku
                withContext(Dispatchers.Main) {
                    finish()
                }
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong(STATE_CREATED_AT, createdAt)
        super.onSaveInstanceState(outState)
    }

    private companion object {
        const val STATE_CREATED_AT = "created_at"
    }
}
