
package org.setu.movieapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val viewMoviesButton =
            findViewById<Button>(R.id.viewMoviesButton)

        val addMovieButton =
            findViewById<Button>(R.id.addMovieButton)

        viewMoviesButton.setOnClickListener {
            startActivity(
                Intent(this, MovieListActivity::class.java)
            )
        }

        addMovieButton.setOnClickListener {
            startActivity(
                Intent(this, AddEditActivity::class.java)
            )
        }
    }
}
