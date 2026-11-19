package com.example.a09noteapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a09noteapp.data.NoteHubDatabaseInstance
import com.example.a09noteapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
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
        val adapter = NoteAdapter(
            onEditClick = { note ->
                val intent = Intent(this, EditNoteActivity::class.java)
                    .putExtra(EditNoteActivity.EXTRA_NOTE_ID, note.id)
                startActivity(intent)
            },
            onDeleteClick = { note ->
                lifecycleScope.launch(Dispatchers.IO) {
                    noteDao.delete(note)
                }
            }
        )
        binding.recyclerViewNotes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNotes.adapter = adapter

        val searchQuery = MutableStateFlow(savedInstanceState?.getString(STATE_SEARCH_QUERY).orEmpty())
        binding.searchViewNotes.setQuery(searchQuery.value, false)
        binding.searchViewNotes.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchViewNotes.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery.value = newText.orEmpty()
                return true
            }
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchQuery.collectLatest { query ->
                    val notes = if (query.isBlank()) noteDao.getAllNotes() else noteDao.searchNotes(query)
                    notes.collect { adapter.submitList(it) }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(STATE_SEARCH_QUERY, binding.searchViewNotes.query.toString())
        super.onSaveInstanceState(outState)
    }

    private companion object {
        const val STATE_SEARCH_QUERY = "search_query"
    }
}
