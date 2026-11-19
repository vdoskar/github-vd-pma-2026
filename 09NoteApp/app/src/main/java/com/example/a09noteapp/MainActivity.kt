package com.example.a09noteapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a09noteapp.data.NoteHubDatabaseInstance
import com.example.a09noteapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Po kliknutí na FAB otevře AddNoteActivity
        binding.fabAddNote.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        val noteDao = NoteHubDatabaseInstance.getDatabase(applicationContext).noteDao()
        val adapter = NoteAdapter(onDeleteClick = { note ->
            lifecycleScope.launch(Dispatchers.IO) {
                noteDao.delete(note)
            }
        })
        binding.recyclerViewNotes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNotes.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                noteDao.getAllNotes().collect { notes ->
                    adapter.submitList(notes)
                }
            }
        }
    }
}
