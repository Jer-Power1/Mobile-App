
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
import java.util.Locale

class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var selectedDateText: TextView
    private lateinit var imagePreview: ImageView
    private lateinit var selectedLocationText: TextView

    private var selectedImageUri: Uri? = null
    private var selectedDay: Int? = null
    private var selectedMonth: Int? = null
    private var editingId: Long? = null

    // Stores the selected cinema coordinates
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null

    // Receives the selected movie poster
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->

            if (uri != null) {

                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) {
                    // Some image providers do not allow this
                }

                selectedImageUri = uri
                imagePreview.setImageURI(uri)
            }
        }

    // Receives the selected coordinates from the map picker
    private val mapPicker =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == RESULT_OK) {

                val data = result.data
                    ?: return@registerForActivityResult

                val latitude =
                    data.getDoubleExtra("latitude", Double.NaN)

                val longitude =
                    data.getDoubleExtra("longitude", Double.NaN)

                // Check that the returned coordinates are valid
                if (latitude.isFinite() &&
                    longitude.isFinite() &&
                    latitude in -90.0..90.0 &&
                    longitude in -180.0..180.0
                ) {
                    selectedLatitude = latitude
                    selectedLongitude = longitude

                    updateLocationText()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_edit)

        titleInput = findViewById(R.id.titleInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        selectedDateText = findViewById(R.id.selectedDateText)
        imagePreview = findViewById(R.id.imageViewAdd)
        selectedLocationText = findViewById(R.id.selectedLocationText)

        val datePickerButton =
            findViewById<Button>(R.id.datePickerButton)

        val imagePickButton =
            findViewById<Button>(R.id.imagePickButton)

        val locationButton =
            findViewById<Button>(R.id.buttonLoc)

        val saveButton =
            findViewById<Button>(R.id.saveButton)

        val cancelButton =
            findViewById<Button>(R.id.cancelButton)

        // Check if an existing movie is being edited
        editingId = intent.getLongExtra("id", -1L)
            .takeIf { it != -1L }

        if (editingId != null) {

            findViewById<TextView>(R.id.formTitle).text = "Edit Movie"

            loadExistingMovie(editingId!!)
        }

        datePickerButton.setOnClickListener {
            showDatePicker()
        }

        imagePickButton.setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

        locationButton.setOnClickListener {
            openMapPicker()
        }

        saveButton.setOnClickListener {
            saveMovie()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    // Opens the map with the current movie details
    private fun openMapPicker() {

        val mapIntent = Intent(
            this,
            MapPickerActivity::class.java
        )

        val latitude = selectedLatitude
        val longitude = selectedLongitude

        // Pass the previously selected location to the map
        if (latitude != null && longitude != null) {

            mapIntent.putExtra("latitude", latitude)
            mapIntent.putExtra("longitude", longitude)
        }

        // Pass the movie title to the selected marker
        mapIntent.putExtra(
            "movieTitle",
            titleInput.text.toString().trim()
        )

        // Pass the movie ID to avoid duplicate markers
        mapIntent.putExtra(
            "movieId",
            editingId ?: -1L
        )

        mapPicker.launch(mapIntent)
    }

    // Opens the release date picker
    private fun showDatePicker() {

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)

        val month =
            (selectedMonth ?: (calendar.get(Calendar.MONTH) + 1)) - 1

        val day =
            selectedDay ?: calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, _, selectedMonthIndex, selectedDayOfMonth ->

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

    // Displays the selected release date
    private fun updateDateText() {

        val day = selectedDay
        val month = selectedMonth

        if (day != null && month != null) {

            val monthNames = listOf(
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
            )

            selectedDateText.text =
                "$day ${monthNames[month - 1]}"
        }
    }

    // Displays the selected cinema coordinates
    private fun updateLocationText() {

        val latitude = selectedLatitude
        val longitude = selectedLongitude

        if (latitude != null && longitude != null) {

            selectedLocationText.text = String.format(
                Locale.US,
                "Latitude: %.5f\nLongitude: %.5f",
                latitude,
                longitude
            )

        } else {

            selectedLocationText.text =
                "No cinema location selected"
        }
    }

    // Loads the details of an existing movie
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

        // Restore the saved cinema location
        selectedLatitude = movie.latitude
        selectedLongitude = movie.longitude

        updateLocationText()

        selectedImageUri = movie.imageUri?.let {
            Uri.parse(it)
        }

        if (selectedImageUri != null) {

            imagePreview.setImageURI(selectedImageUri)

        } else {

            imagePreview.setImageResource(
                android.R.drawable.ic_menu_gallery
            )
        }
    }

    // Validates and saves the movie
    private fun saveMovie() {

        val title = titleInput.text.toString().trim()

        val description =
            descriptionInput.text.toString().trim()

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

        // Save all movie details including the cinema location
        val movie = Movie(
            id = editingId ?: 0L,
            title = title,
            description = description,
            releaseDay = day.toDouble(),
            releaseMonth = month.toDouble(),
            imageUri = selectedImageUri?.toString(),
            latitude = selectedLatitude,
            longitude = selectedLongitude
        )

        try {

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

        } catch (e: IllegalArgumentException) {

            Toast.makeText(
                this,
                e.message ?: "Invalid movie details",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
