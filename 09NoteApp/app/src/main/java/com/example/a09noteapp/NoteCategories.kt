package com.example.a09noteapp

import android.widget.ArrayAdapter
import android.widget.Spinner

internal fun Spinner.showNoteCategories(selectedCategory: String? = null) {
    val defaults = resources.getStringArray(R.array.note_categories).toList()
    val categories = if (selectedCategory != null && selectedCategory !in defaults) {
        defaults + selectedCategory
    } else {
        defaults
    }
    adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, categories).apply {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }
    if (selectedCategory != null) setSelection(categories.indexOf(selectedCategory))
}
