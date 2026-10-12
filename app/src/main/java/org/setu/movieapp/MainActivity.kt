
package org.setu.movieapp

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var listLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createUserInterface()
    }

    override fun onResume() {
        super.onResume()

        if (::listLayout.isInitialized) {
            displayMovies()
        }
    }

    private fun createUserInterface() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 320, 32, 32)
        }

        val title = TextView(this).apply {
            text = "Upcoming Movies"
            textSize = 28f
            gravity = Gravity.CENTER
        }

        val addButton = Button(this).apply {
            text = "Add Movie"
            setOnClickListener {
                val intent = Intent(this@MainActivity, AddEditActivity::class.java)
                startActivity(intent)
            }
        }

        listLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            addButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            listLayout,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        setContentView(root)

        displayMovies()
    }

    private fun displayMovies() {

        listLayout.removeAllViews()

        val movies = AppData.movies.findAll()

        if (movies.isEmpty()) {

            val emptyText = TextView(this).apply {
                text = "No movies added yet."
                textSize = 18f
                setPadding(0, 40, 0, 40)
            }

            listLayout.addView(emptyText)

            return
        }

        for (movie in movies) {

            val movieLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 20, 0, 20)
            }

            val movieTitle = TextView(this).apply {
                text = "${movie.id}: ${movie.title}"
                textSize = 20f
            }

            val movieDescription = TextView(this).apply {
                text = movie.description
                textSize = 16f
            }

            val releaseDate = TextView(this).apply {
                text = "Release: ${movie.releaseDay.toInt()}/${movie.releaseMonth.toInt()}"
                textSize = 14f
            }

            val editButton = Button(this).apply {
                text = "Edit"

                setOnClickListener {
                    val intent = Intent(
                        this@MainActivity,
                        AddEditActivity::class.java
                    )

                    intent.putExtra("id", movie.id)

                    startActivity(intent)
                }
            }

            val deleteButton = Button(this).apply {
                text = "Delete"

                setOnClickListener {
                    AppData.movies.delete(movie.id)
                    displayMovies()
                }
            }

            movieLayout.addView(movieTitle)
            movieLayout.addView(movieDescription)
            movieLayout.addView(releaseDate)
            movieLayout.addView(editButton)
            movieLayout.addView(deleteButton)

            listLayout.addView(movieLayout)
        }
    }
}
