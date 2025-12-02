package com.example.notesapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapplication.model.Note
import com.example.notesapplication.vm.NotesViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val vm: NotesViewModel by viewModels()

    private lateinit var etSearch: EditText
    private lateinit var chipGroup: LinearLayout
    private lateinit var rvNotes: RecyclerView
    private lateinit var fabAdd: FloatingActionButton

    private lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etSearch = findViewById(R.id.etSearch)
        chipGroup = findViewById(R.id.chipGroup)
        rvNotes = findViewById(R.id.rvNotes)
        fabAdd = findViewById(R.id.fabAdd)

        adapter = NotesAdapter(
            onDelete = { note -> vm.delete(note) }
        )
        rvNotes.layoutManager = LinearLayoutManager(this)
        rvNotes.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                vm.setSearch(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        fabAdd.setOnClickListener {
            showAddNoteDialog()
        }

        lifecycleScope.launch {
            vm.notes.collectLatest { notes ->
                adapter.submitList(notes)
                buildTagChips(notes)
            }
        }
    }

    private fun buildTagChips(notes: List<Note>) {
        chipGroup.removeAllViews()

        val allTags = notes.flatMap { it.tags }.distinct()
        val inflater = LayoutInflater.from(this)

        val allChip = Chip(this).apply {
            text = "all"
            isCheckable = true
            isChecked = vm.selectedTag.value == null
            setOnClickListener { vm.setTag(null) }
            setPadding(16)
        }
        chipGroup.addView(allChip)

        allTags.forEach { tag ->
            val chip = Chip(this).apply {
                text = tag
                isCheckable = true
                isChecked = vm.selectedTag.value == tag
                setOnClickListener {
                    val newTag = if (vm.selectedTag.value == tag) null else tag
                    vm.setTag(newTag)
                }
                setPadding(16)
            }
            chipGroup.addView(chip)
        }
    }

    private fun showAddNoteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.etTitle)
        val etBody = dialogView.findViewById<EditText>(R.id.etBody)
        val etTags = dialogView.findViewById<EditText>(R.id.etTags)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Note")
            .setView(dialogView)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val btnAdd: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnAdd.setOnClickListener {
                val title = etTitle.text.toString().trim()
                val body = etBody.text.toString().trim()
                val tagsCsv = etTags.text.toString().trim()

                if (title.isNotBlank()) {
                    val tags = tagsCsv.split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    vm.add(
                        Note(
                            title = title,
                            body = body,
                            tags = tags
                        )
                    )
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
}
