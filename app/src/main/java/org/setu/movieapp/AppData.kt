
package org.setu.movieapp

import android.content.Context
import java.io.File

object AppData {

    lateinit var movies: MovieList
        private set

    // Initialise the movie storage
    fun initialize(context: Context) {

        // Prevent the movie list from being loaded twice
        if (::movies.isInitialized) {
            return
        }

        // Create the JSON file in internal app storage
        val file = File(
            context.filesDir,
            "movie_list.json"
        )

        movies = MovieList(file)
    }
}
