
package org.setu.movieapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class MovieListActivity : AppCompatActivity(), OnMovieListener {

    private lateinit var adapter: MovieAdapter
    private lateinit var monthSpinner: Spinner

    private val months = listOf(
        "All Months",
        "January", "February", "March", "April",
        "May", "June", "July", "August",
        "September", "October", "November", "December"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_movie_list)

        val recyclerView =
            findViewById<RecyclerView>(R.id.moviesRecyclerView)

        adapter = MovieAdapter(
            movies = AppData.movies.findAll(),
            listener = this
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Set up the month dropdown
        monthSpinner = findViewById(R.id.monthSpinner)

        val spinnerAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            months
        ) {
            override fun getView(
                position: Int,
                convertView: View?,
                parent: android.view.ViewGroup
            ): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).setTextColor(android.graphics.Color.WHITE)
                return view
            }
        }

        spinnerAdapter.setDropDownViewResource(
            R.layout.item_spinner_month
        )

        monthSpinner.adapter = spinnerAdapter

        monthSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    filterMovies(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    filterMovies(0)
                }
            }

        val returnButton =
            findViewById<Button>(R.id.returnButton)

        returnButton.setOnClickListener {
            finish()
        }
    }

    private fun filterMovies(selectedMonth: Int) {
        val allMovies = AppData.movies.findAll()

        val filteredMovies = if (selectedMonth == 0) {
            allMovies
        } else {
            allMovies.filter {
                it.releaseMonth.toInt() == selectedMonth
            }
        }

        adapter.updateMovies(filteredMovies)

        Timber.i("Filtering movies by: ${months[selectedMonth]}")
    }

    override fun onMovieClick(movie: Movie) {
        Timber.i("Editing movie: ${movie.title}")

        val intent = Intent(this, AddEditActivity::class.java)
        intent.putExtra("id", movie.id)
        startActivity(intent)
    }

    override fun onMovieLongClick(movie: Movie): Boolean {
        AlertDialog.Builder(this)
            .setTitle("Delete Movie")
            .setMessage("Are you sure you want to delete ${movie.title}?")
            .setPositiveButton("Delete") { _, _ ->
                Timber.i("Deleting movie: ${movie.title}")

                AppData.movies.delete(movie.id)

                // Keep the selected month filter active
                filterMovies(monthSpinner.selectedItemPosition)
            }
            .setNegativeButton("Cancel", null)
            .show()

        return true
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized && ::monthSpinner.isInitialized) {
            filterMovies(monthSpinner.selectedItemPosition)
        }
    }
}
