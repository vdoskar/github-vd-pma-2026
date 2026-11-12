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

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = NoteHubDatabaseInstance.getDatabase(this)
        val noteDao = db.noteDao()

        binding.btnSaveNote.setOnClickListener {
            val title = binding.editTextTitle.text.toString()
            val content = binding.editTextContent.text.toString()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, R.string.note_fields_required, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val note = Note(title = title, content = content)

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
}
