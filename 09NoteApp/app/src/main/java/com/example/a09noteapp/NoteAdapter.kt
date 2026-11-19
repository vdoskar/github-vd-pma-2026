package com.example.a09noteapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.a09noteapp.data.Note
import com.example.a09noteapp.databinding.ItemNoteBinding

class NoteAdapter(
    private val onDeleteClick: (Note) -> Unit,
    private val onEditClick: (Note) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiff) {

    class NoteViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.binding.tvNoteTitle.text = note.title
        holder.binding.tvNoteContent.text = note.content
        holder.binding.tvNoteCategory.text = note.category
        holder.binding.ivEdit.setOnClickListener { onEditClick(note) }
        holder.binding.ivDelete.setOnClickListener { onDeleteClick(note) }
    }

    private object NoteDiff : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean =
            oldItem == newItem
    }
}
