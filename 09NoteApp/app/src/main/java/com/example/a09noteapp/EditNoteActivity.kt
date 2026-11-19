package com.example.a09noteapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.a09noteapp.data.NoteHubDatabaseInstance
import com.example.a09noteapp.databinding.ActivityEditNoteBinding
import kotlinx.coroutines.launch

class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private var formLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
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
            val selectedCategory = savedInstanceState?.getString(STATE_CATEGORY) ?: note.category
            binding.spinnerCategory.showNoteCategories(selectedCategory)
            formLoaded = true
            binding.etEditTitle.isEnabled = true
            binding.etEditContent.isEnabled = true
            binding.spinnerCategory.isEnabled = true
            binding.btnSaveChanges.isEnabled = true

            binding.btnSaveChanges.setOnClickListener {
                val title = binding.etEditTitle.text.toString()
                val content = binding.etEditContent.text.toString()
                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(this@EditNoteActivity, R.string.note_fields_required, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                binding.btnSaveChanges.isEnabled = false
                val category = binding.spinnerCategory.selectedItem.toString()
                lifecycleScope.launch {
                    val updatedRows = noteDao.update(
                        note.copy(title = title, content = content, category = category)
                    )
                    if (updatedRows == 0) closeMissingNote() else finish()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_FORM_LOADED, formLoaded)
        if (formLoaded) outState.putString(STATE_CATEGORY, binding.spinnerCategory.selectedItem.toString())
        super.onSaveInstanceState(outState)
    }

    private fun closeMissingNote() {
        Toast.makeText(this, R.string.note_not_found, Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        const val EXTRA_NOTE_ID = "note_id"
        private const val STATE_FORM_LOADED = "form_loaded"
        private const val STATE_CATEGORY = "category"
    }
}
