package com.example.notesapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapplication.model.Note
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*

class NotesAdapter(
    private val onDelete: (Note) -> Unit
) : ListAdapter<Note, NotesAdapter.NoteViewHolder>(Diff) {

    object Diff : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view, onDelete)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NoteViewHolder(
        itemView: View,
        private val onDelete: (Note) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvBody: TextView = itemView.findViewById(R.id.tvBody)
        private val tagsContainer: LinearLayout = itemView.findViewById(R.id.tagsContainer)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvDelete: TextView = itemView.findViewById(R.id.tvDelete)
        private val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun bind(note: Note) {
            tvTitle.text = note.title
            tvBody.text = note.body
            tvDate.text = sdf.format(Date(note.createdAt))

            tagsContainer.removeAllViews()
            val ctx = itemView.context
            note.tags.forEach { tag ->
                val chip = Chip(ctx).apply {
                    text = tag
                    isClickable = false
                }
                tagsContainer.addView(chip)
            }

            tvDelete.setOnClickListener {
                onDelete(note)
            }
        }
    }
}