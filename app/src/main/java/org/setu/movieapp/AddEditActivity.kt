
package org.setu.movieapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var selectedDateText: TextView

    private var selectedDay: Int? = null
    private var selectedMonth: Int? = null

    private var editingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_edit)

        titleInput = findViewById(R.id.titleInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        selectedDateText = findViewById(R.id.selectedDateText)

        val datePickerButton =
            findViewById<Button>(R.id.datePickerButton)

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

        // Open the calendar when the button is tapped
        datePickerButton.setOnClickListener {
            showDatePicker()
        }

        saveButton.setOnClickListener {
            saveMovie()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = (selectedMonth ?: (calendar.get(Calendar.MONTH) + 1)) - 1
        val day = selectedDay ?: calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonthIndex, selectedDayOfMonth ->

                selectedDay = selectedDayOfMonth
                selectedMonth = selectedMonthIndex + 1

                updateDateText()

            },
            year,
            month,
            day
        )

        datePicker.show()
    }

    private fun updateDateText() {
        val day = selectedDay
        val month = selectedMonth

        if (day != null && month != null) {
            val monthNames = listOf(
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
            )

            selectedDateText.text =
                "$day ${monthNames[month - 1]}"
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

        selectedDay = movie.releaseDay.toInt()
        selectedMonth = movie.releaseMonth.toInt()

        updateDateText()
    }

    private fun saveMovie() {
        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        if (title.isEmpty()) {
            titleInput.error = "Title is required"
            return
        }

        val day = selectedDay
        val month = selectedMonth

        if (day == null || month == null) {
            Toast.makeText(
                this,
                "Please select a release date",
                Toast.LENGTH_SHORT
            ).show()
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
