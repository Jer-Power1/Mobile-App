
package org.setu.movieapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class MovieListActivity : AppCompatActivity(), OnMovieListener {

    private lateinit var adapter: MovieAdapter

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

        val returnButton =
            findViewById<Button>(R.id.returnButton)

        returnButton.setOnClickListener {
            finish()
        }
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
                adapter.updateMovies(AppData.movies.findAll())
            }
            .setNegativeButton("Cancel", null)
            .show()

        return true
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            adapter.updateMovies(AppData.movies.findAll())
        }
    }
}
