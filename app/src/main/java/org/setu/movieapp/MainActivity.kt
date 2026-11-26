
package org.setu.movieapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Set up Timber logging
        if (Timber.treeCount == 0) {
            Timber.plant(Timber.DebugTree())
        }

        // Load the saved movie data
        AppData.initialize(this)

        val viewMoviesButton =
            findViewById<Button>(R.id.viewMoviesButton)

        val addMovieButton =
            findViewById<Button>(R.id.addMovieButton)

        // Opens the movie list
        viewMoviesButton.setOnClickListener {
            startActivity(
                Intent(this, MovieListActivity::class.java)
            )
        }

        // Opens the add movie screen
        addMovieButton.setOnClickListener {
            startActivity(
                Intent(this, AddEditActivity::class.java)
            )
        }

        Timber.i("Movie App started")
    }
}
