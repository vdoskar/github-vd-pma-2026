package com.example.a09noteapp

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.hasSibling
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.a09noteapp.data.Note
import com.example.a09noteapp.data.NoteHubDatabaseInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddNoteFlowTest {
    @Test
    fun requiresBothFieldsAndPersistsNoteBeforeReturning() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val noteDao = NoteHubDatabaseInstance.getDatabase(context).noteDao()
        val title = "Test note ${System.nanoTime()}"
        val content = "Content saved from the add note form"

        try {
            ActivityScenario.launch(MainActivity::class.java).use { scenario ->
                onView(withId(R.id.fabAddNote)).perform(click())

                onView(withId(R.id.btnSaveNote)).perform(scrollTo(), click())
                onView(withId(R.id.editTextTitle)).check(matches(isDisplayed()))

                onView(withId(R.id.editTextTitle))
                    .perform(replaceText(title), closeSoftKeyboard())
                onView(withId(R.id.btnSaveNote)).perform(scrollTo(), click())
                onView(withId(R.id.editTextContent)).check(matches(isDisplayed()))

                onView(withId(R.id.editTextContent))
                    .perform(replaceText(content), closeSoftKeyboard())
                onView(withId(R.id.spinnerCategory)).perform(scrollTo(), click())
                onData(equalTo("Škola")).perform(click())
                onView(withId(R.id.btnSaveNote)).perform(scrollTo(), click())

                val savedNote = runBlocking {
                    withTimeout(10_000) {
                        noteDao.getAllNotes().first { notes -> notes.any { it.title == title } }
                            .single { it.title == title }
                    }
                }
                assertEquals(content, savedNote.content)
                assertEquals("Škola", savedNote.category)
                onView(withId(R.id.fabAddNote)).check(matches(isDisplayed()))

                val updatedContent = "Content updated directly in Room"
                runBlocking {
                    awaitFirstNote(scenario, title, content)
                    noteDao.update(savedNote.copy(content = updatedContent))
                    awaitFirstNote(scenario, title, updatedContent)
                }
            }
        } finally {
            runBlocking {
                noteDao.getAllNotes().first().filter { it.title == title }
                    .forEach { noteDao.delete(it) }
            }
        }
    }

    @Test
    fun deletesOnlyTheSelectedNote() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val noteDao = NoteHubDatabaseInstance.getDatabase(context).noteDao()
        val testId = System.nanoTime()
        val keptNote = Note(title = "Keep $testId", content = "Keep this note")
        val deletedNote = Note(title = "Delete $testId", content = "Delete this note")
        val testTitles = setOf(keptNote.title, deletedNote.title)

        try {
            runBlocking {
                noteDao.insert(keptNote)
                noteDao.insert(deletedNote)
            }
            ActivityScenario.launch(MainActivity::class.java).use { scenario ->
                runBlocking { awaitFirstNote(scenario, deletedNote.title, deletedNote.content) }
                onView(allOf(withId(R.id.ivDelete), hasSibling(hasDescendant(withText(deletedNote.title)))))
                    .perform(click())

                runBlocking {
                    awaitFirstNote(scenario, keptNote.title, keptNote.content)
                    val remainingTitles = noteDao.getAllNotes().first()
                        .map { it.title }.filter { it in testTitles }
                    assertEquals(listOf(keptNote.title), remainingTitles)
                }
            }
        } finally {
            runBlocking {
                noteDao.getAllNotes().first().filter { it.title in testTitles }
                    .forEach { noteDao.delete(it) }
            }
        }
    }

    @Test
    fun editsTheSelectedNoteWithoutCreatingANewRow() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val noteDao = NoteHubDatabaseInstance.getDatabase(context).noteDao()
        val testId = System.nanoTime()
        val original = Note(title = "Original $testId", content = "Original content", category = "Škola")
        val newTitle = "Edited $testId"
        val newContent = "Edited content"

        try {
            val savedNote = runBlocking {
                noteDao.insert(original)
                noteDao.getAllNotes().first().single { it.title == original.title }
            }
            ActivityScenario.launch(MainActivity::class.java).use { scenario ->
                runBlocking { awaitFirstNote(scenario, original.title, original.content) }
                onView(allOf(withId(R.id.ivEdit), hasSibling(hasDescendant(withText(original.title)))))
                    .perform(click())

                onView(withId(R.id.etEditTitle)).check(matches(withText(original.title)))
                onView(withId(R.id.etEditContent)).check(matches(withText(original.content)))
                onView(withId(R.id.spinnerCategory)).check(matches(hasDescendant(withText("Škola"))))
                onView(withId(R.id.etEditTitle)).perform(replaceText(""), closeSoftKeyboard())
                onView(withId(R.id.btnSaveChanges)).perform(scrollTo(), click())
                onView(withId(R.id.etEditContent)).check(matches(isDisplayed()))
                onView(withId(R.id.etEditTitle)).perform(replaceText(newTitle), closeSoftKeyboard())
                onView(withId(R.id.etEditContent)).perform(replaceText(newContent), closeSoftKeyboard())
                onView(withId(R.id.spinnerCategory)).perform(scrollTo(), click())
                onData(equalTo("Práce")).perform(click())
                onView(withId(R.id.btnSaveChanges)).perform(scrollTo(), click())

                runBlocking {
                    awaitFirstNote(scenario, newTitle, newContent)
                    val notes = noteDao.getAllNotes().first()
                        .filter { it.title == original.title || it.title == newTitle }
                    assertEquals(listOf(savedNote.copy(title = newTitle, content = newContent, category = "Práce")), notes)
                }
                onView(allOf(withId(R.id.tvNoteCategory), hasSibling(withText(newTitle))))
                    .check(matches(withText("Práce")))
                onView(withId(R.id.fabAddNote)).check(matches(isDisplayed()))
            }
        } finally {
            runBlocking {
                noteDao.getAllNotes().first()
                    .filter { it.title == original.title || it.title == newTitle }
                    .forEach { noteDao.delete(it) }
            }
        }
    }

    @Test
    fun searchesTitleAndContentAndClearsTheFilter() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val noteDao = NoteHubDatabaseInstance.getDatabase(context).noteDao()
        val testId = System.nanoTime()
        val query = "match_$testId"
        val titleMatch = Note(title = "Title $query", content = "Unrelated content")
        val contentMatch = Note(title = "Content match $testId", content = "Contains $query")
        val noMatch = Note(title = "Title matchX$testId", content = "Unrelated")
        val fixtures = listOf(titleMatch, contentMatch, noMatch)
        val titles = fixtures.map { it.title }

        try {
            runBlocking { fixtures.forEach { noteDao.insert(it) } }
            ActivityScenario.launch(MainActivity::class.java).use { scenario ->
                val search = onView(withId(androidx.appcompat.R.id.search_src_text))
                search.perform(replaceText(query), closeSoftKeyboard())
                runBlocking {
                    awaitSearchResults(scenario, titles, listOf(contentMatch.title, titleMatch.title))
                    val savedNote = noteDao.searchNotes(query).first().first { it.title == contentMatch.title }
                    noteDao.update(savedNote.copy(content = "No longer matches"))
                    awaitSearchResults(scenario, titles, listOf(titleMatch.title))
                }
                search.perform(replaceText(""), closeSoftKeyboard())
                runBlocking { awaitSearchResults(scenario, titles, titles.reversed()) }
            }
        } finally {
            runBlocking {
                noteDao.getAllNotes().first().filter { it.title in titles }
                    .forEach { noteDao.delete(it) }
            }
        }
    }

    private suspend fun awaitSearchResults(
        scenario: ActivityScenario<MainActivity>,
        fixtureTitles: List<String>,
        expected: List<String>
    ) = withTimeout(10_000) {
        var actual = emptyList<String>()
        while (actual != expected) {
            scenario.onActivity { activity ->
                val list = activity.findViewById<RecyclerView>(R.id.recyclerViewNotes)
                actual = (list.adapter as NoteAdapter).currentList.map { it.title }
                    .filter { it in fixtureTitles }
            }
            if (actual != expected) delay(50)
        }
    }

    private suspend fun awaitFirstNote(
        scenario: ActivityScenario<MainActivity>,
        title: String,
        content: String
    ) = withTimeout(10_000) {
        var matchesNote = false
        while (!matchesNote) {
            scenario.onActivity { activity ->
                val list = activity.findViewById<RecyclerView>(R.id.recyclerViewNotes)
                val item = list.findViewHolderForAdapterPosition(0)?.itemView
                matchesNote = item?.findViewById<TextView>(R.id.tvNoteTitle)?.text == title &&
                    item?.findViewById<TextView>(R.id.tvNoteContent)?.text == content
            }
            if (!matchesNote) delay(50)
        }
    }
}
