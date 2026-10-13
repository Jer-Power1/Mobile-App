
package org.setu.movieapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MovieListActivity : AppCompatActivity() {

    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_movie_list)

        val recyclerView =
            findViewById<RecyclerView>(R.id.moviesRecyclerView)

        adapter = MovieAdapter(
            movies = AppData.movies.findAll(),
            onEdit = { movie ->
                val intent = Intent(this, AddEditActivity::class.java)
                intent.putExtra("id", movie.id)
                startActivity(intent)
            },
            onDelete = { movie ->
                AppData.movies.delete(movie.id)
                adapter.updateMovies(AppData.movies.findAll())
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val returnButton =
            findViewById<Button>(R.id.returnButton)

        returnButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            adapter.updateMovies(AppData.movies.findAll())
        }
    }
}
