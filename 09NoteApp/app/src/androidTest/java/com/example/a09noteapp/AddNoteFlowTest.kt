package com.example.a09noteapp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.a09noteapp.data.NoteHubDatabaseInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
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
            ActivityScenario.launch(MainActivity::class.java).use {
                onView(withId(R.id.fabAddNote)).perform(click())

                onView(withId(R.id.btnSaveNote)).perform(scrollTo(), click())
                onView(withId(R.id.editTextTitle)).check(matches(isDisplayed()))

                onView(withId(R.id.editTextTitle))
                    .perform(replaceText(title), closeSoftKeyboard())
                onView(withId(R.id.btnSaveNote)).perform(scrollTo(), click())
                onView(withId(R.id.editTextContent)).check(matches(isDisplayed()))

                onView(withId(R.id.editTextContent))
                    .perform(replaceText(content), closeSoftKeyboard())
                onView(withId(R.id.btnSaveNote)).perform(scrollTo(), click())

                val savedNote = runBlocking {
                    withTimeout(10_000) {
                        noteDao.getAllNotes().first { notes -> notes.any { it.title == title } }
                            .single { it.title == title }
                    }
                }
                assertEquals(content, savedNote.content)
                onView(withId(R.id.fabAddNote)).check(matches(isDisplayed()))
            }
        } finally {
            runBlocking {
                noteDao.getAllNotes().first().filter { it.title == title }
                    .forEach { noteDao.delete(it) }
            }
        }
    }
}
