package com.example.a09noteapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.a09noteapp.data.NoteHubDatabaseInstance
import com.example.a09noteapp.databinding.ActivityEditNoteBinding
import kotlinx.coroutines.launch

class EditNoteActivity : AppCompatActivity() {
    private var formLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val noteId = intent.getIntExtra(EXTRA_NOTE_ID, -1)
        if (noteId <= 0) {
            closeMissingNote()
            return
        }

        val noteDao = NoteHubDatabaseInstance.getDatabase(applicationContext).noteDao()
        lifecycleScope.launch {
            val note = noteDao.getNoteById(noteId)
            if (note == null) {
                closeMissingNote()
                return@launch
            }

            if (savedInstanceState?.getBoolean(STATE_FORM_LOADED) != true) {
                binding.etEditTitle.setText(note.title)
                binding.etEditContent.setText(note.content)
            }
            formLoaded = true
            binding.etEditTitle.isEnabled = true
            binding.etEditContent.isEnabled = true
            binding.btnSaveChanges.isEnabled = true

            binding.btnSaveChanges.setOnClickListener {
                val title = binding.etEditTitle.text.toString()
                val content = binding.etEditContent.text.toString()
                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(this@EditNoteActivity, R.string.note_fields_required, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                binding.btnSaveChanges.isEnabled = false
                lifecycleScope.launch {
                    val updatedRows = noteDao.update(note.copy(title = title, content = content))
                    if (updatedRows == 0) closeMissingNote() else finish()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_FORM_LOADED, formLoaded)
        super.onSaveInstanceState(outState)
    }

    private fun closeMissingNote() {
        Toast.makeText(this, R.string.note_not_found, Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        const val EXTRA_NOTE_ID = "note_id"
        private const val STATE_FORM_LOADED = "form_loaded"
    }
}
