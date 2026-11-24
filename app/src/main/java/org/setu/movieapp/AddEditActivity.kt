
package org.setu.movieapp

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class AddEditActivity : AppCompatActivity() {

    // Inputs for the movie details
    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var selectedDateText: TextView

    // Used to display and store the selected image
    private lateinit var imagePreview: ImageView
    private var selectedImageUri: Uri? = null

    // Stores the selected day and month
    private var selectedDay: Int? = null
    private var selectedMonth: Int? = null

    // Used when editing an existing movie
    private var editingId: Long? = null

    // Gets the image selected by the user
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->

            if (uri != null) {

                // Tries to keep permission to access the image later
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                    // Some image providers do not allow this
                }

                // Stores the image URI
                selectedImageUri = uri

                // Shows the selected image
                imagePreview.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Loads the add/edit movie layout
        setContentView(R.layout.activity_add_edit)

        // Connects the inputs to the XML layout
        titleInput = findViewById(R.id.titleInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        selectedDateText = findViewById(R.id.selectedDateText)
        imagePreview = findViewById(R.id.imageViewAdd)

        // Gets the buttons from the layout
        val datePickerButton =
            findViewById<Button>(R.id.datePickerButton)

        val imagePickButton =
            findViewById<Button>(R.id.imagePickButton)

        val saveButton =
            findViewById<Button>(R.id.saveButton)

        val cancelButton =
            findViewById<Button>(R.id.cancelButton)

        // Gets the movie ID if editing an existing movie
        editingId = intent.getLongExtra("id", -1L)
            .takeIf { it != -1L }

        // Loads the existing movie details
        if (editingId != null) {
            findViewById<TextView>(R.id.formTitle).text =
                "Edit Movie"

            loadExistingMovie(editingId!!)
        }

        // Opens the calendar
        datePickerButton.setOnClickListener {
            showDatePicker()
        }

        // Opens the picker and only allows image files
        imagePickButton.setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

        // Saves the movie
        saveButton.setOnClickListener {
            saveMovie()
        }

        // Goes back without saving
        cancelButton.setOnClickListener {
            finish()
        }
    }

    // Opens the calendar to select a release date
    private fun showDatePicker() {

        // Gets today's date
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)

        // Uses the selected month or today's month
        val month =
            (selectedMonth ?: (calendar.get(Calendar.MONTH) + 1)) - 1

        // Uses the selected day or today's day
        val day =
            selectedDay ?: calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonthIndex, selectedDayOfMonth ->

                // Stores the selected day
                selectedDay = selectedDayOfMonth

                // Adds 1 because January starts at 0
                selectedMonth = selectedMonthIndex + 1

                // Updates the date shown on screen
                updateDateText()
            },
            year,
            month,
            day
        )

        // Shows the calendar
        datePicker.show()
    }

    // Displays the selected release date
    private fun updateDateText() {
        val day = selectedDay
        val month = selectedMonth

        if (day != null && month != null) {

            // List of month names
            val monthNames = listOf(
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
            )

            // Shows the day and month name
            selectedDateText.text =
                "$day ${monthNames[month - 1]}"
        }
    }

    // Loads the details of an existing movie
    private fun loadExistingMovie(id: Long) {

        // Finds the movie using its ID
        val movie = AppData.movies.findOne(id)

        // Goes back if the movie cannot be found
        if (movie == null) {
            Toast.makeText(
                this,
                "Movie not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        // Fills in the existing movie details
        titleInput.setText(movie.title)
        descriptionInput.setText(movie.description)

        selectedDay = movie.releaseDay.toInt()
        selectedMonth = movie.releaseMonth.toInt()

        updateDateText()
    }

    // Checks the movie details and saves them
    private fun saveMovie() {

        // Gets the text entered by the user
        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        // Makes sure the title is not empty
        if (title.isEmpty()) {
            titleInput.error = "Title is required"
            return
        }

        val day = selectedDay
        val month = selectedMonth

        // Makes sure a release date has been selected
        if (day == null || month == null) {
            Toast.makeText(
                this,
                "Please select a release date",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Creates a movie using the entered details
        val movie = Movie(
            id = editingId ?: 0L,
            title = title,
            description = description,
            releaseDay = day.toDouble(),
            releaseMonth = month.toDouble()
        )

        // Adds a new movie if there is no editing ID
        if (editingId == null) {
            AppData.movies.create(movie)

            Toast.makeText(
                this,
                "Movie created",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            // Updates the existing movie
            AppData.movies.update(movie)

            Toast.makeText(
                this,
                "Movie updated",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Returns to the previous screen
        finish()
    }
}
