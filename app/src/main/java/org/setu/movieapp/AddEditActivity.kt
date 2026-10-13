
package org.setu.movieapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

        setContentView(R.layout.activity_add_edit)

        titleInput = findViewById(R.id.titleInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        dayInput = findViewById(R.id.dayInput)
        monthInput = findViewById(R.id.monthInput)

        val saveButton =
            findViewById<Button>(R.id.saveButton)

        val cancelButton =
            findViewById<Button>(R.id.cancelButton)

        editingId = intent.getLongExtra("id", -1L)
            .takeIf { it != -1L }

        if (editingId != null) {
            findViewById<TextView>(R.id.formTitle).text =
                "Edit Movie"

            loadExistingMovie(editingId!!)
        }

        saveButton.setOnClickListener {
            saveMovie()
        }

        cancelButton.setOnClickListener {
            finish()
        }
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
        val description =
            descriptionInput.text.toString().trim()

        if (title.isEmpty()) {
            titleInput.error = "Title is required"
            return
        }

        val day = dayInput.text.toString().toIntOrNull()

        if (day == null || day !in 1..31) {
            dayInput.error = "Enter a day from 1 to 31"
            return
        }

        val month = monthInput.text.toString().toIntOrNull()

        if (month == null || month !in 1..12) {
            monthInput.error = "Enter a month from 1 to 12"
            return
        }

        val movie = Movie(
            id = editingId ?: 0L,
            title = title,
            description = description,
            releaseDay = day.toDouble(),
            releaseMonth = month.toDouble()
        )

        if (editingId == null) {
            AppData.movies.create(movie)

            Toast.makeText(
                this,
                "Movie created",
                Toast.LENGTH_SHORT
            ).show()
        } else {
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
