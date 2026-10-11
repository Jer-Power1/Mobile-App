
package org.setu.movieapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var dayInput: EditText
    private lateinit var monthInput: EditText

    private var editingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createUserInterface()

        editingId = intent.getLongExtra("id", -1L)

        if (editingId != -1L) {
            loadExistingMovie(editingId!!)
        }
    }

    private fun createUserInterface() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 320, 32, 32)
        }

        titleInput = EditText(this).apply {
            hint = "Movie Title"
        }

        descriptionInput = EditText(this).apply {
            hint = "Description"
        }

        dayInput = EditText(this).apply {
            hint = "Release Day"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        monthInput = EditText(this).apply {
            hint = "Release Month"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        val saveButton = Button(this).apply {
            text = "Save"

            setOnClickListener {
                saveMovie()
            }
        }

        val cancelButton = Button(this).apply {
            text = "Cancel"

            setOnClickListener {
                finish()
            }
        }

        root.addView(titleInput)
        root.addView(descriptionInput)
        root.addView(dayInput)
        root.addView(monthInput)
        root.addView(saveButton)
        root.addView(cancelButton)

        setContentView(root)
    }

    private fun loadExistingMovie(id: Long) {

        val movie = AppData.movies.findOne(id)

        if (movie == null) {
            Toast.makeText(
                this,
                "Movie not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        titleInput.setText(movie.title)
        descriptionInput.setText(movie.description)
        dayInput.setText(movie.releaseDay.toInt().toString())
        monthInput.setText(movie.releaseMonth.toInt().toString())
    }

    private fun saveMovie() {

        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        if (title.isEmpty()) {
            titleInput.error = "Movie title is required"
            return
        }

        val day = dayInput.text.toString().toDoubleOrNull()

        if (day == null || day < 1 || day > 31) {
            dayInput.error = "Enter a valid day (1-31)"
            return
        }

        val month = monthInput.text.toString().toDoubleOrNull()

        if (month == null || month < 1 || month > 12) {
            monthInput.error = "Enter a valid month (1-12)"
            return
        }

        if (editingId == null || editingId == -1L) {

            val movie = Movie(
                title = title,
                description = description,
                releaseDay = day,
                releaseMonth = month
            )

            AppData.movies.create(movie)

            Toast.makeText(
                this,
                "Movie created",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            val movie = Movie(
                id = editingId!!,
                title = title,
                description = description,
                releaseDay = day,
                releaseMonth = month
            )

            AppData.movies.update(movie)

            Toast.makeText(
                this,
                "Movie updated",
                Toast.LENGTH_SHORT
            ).show()
        }

        finish()
    }
}
