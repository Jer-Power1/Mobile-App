
package org.setu.movieapp

import android.util.AtomicFile
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class MovieList(private val file: File) {

    // Stores all movies in memory
    private val movies = ArrayList<Movie>()

    // Converts movies to and from JSON
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    // Helps protect the file during saving
    private val atomicFile = AtomicFile(file)

    // Stores any error that occurred during loading
    var loadError: String? = null
        private set

    init {
        load()
    }

    // Loads movies from the JSON file
    private fun load() {

        try {

            val json = atomicFile.openRead()
                .bufferedReader()
                .use { it.readText() }

            val type =
                object : TypeToken<ArrayList<Movie>>() {}.type

            val loaded: ArrayList<Movie> =
                gson.fromJson<ArrayList<Movie>>(json, type)
                    ?: throw IOException("Invalid movie data")

            // Validate the saved movie details
            loaded.forEach { it.validate() }

            require(loaded.all { it.id > 0L })
            require(loaded.map { it.id }.distinct().size == loaded.size)

            movies.clear()
            movies.addAll(loaded)

            Timber.i("Loaded ${movies.size} movies")

        } catch (e: FileNotFoundException) {

            if (file.exists() ||
                File(file.path + ".bak").exists()
            ) {
                loadError = "Unable to read saved movies."
                Timber.e(e, "Unable to read movie file")
            } else {
                Timber.i("No saved movie file found")
            }

        } catch (e: Exception) {

            loadError = "Unable to load saved movies."

            Timber.e(e, "Error loading movie data")
        }
    }

    // Saves the updated movie list safely
    private fun save(updatedMovies: List<Movie>) {

        // Do not overwrite data that failed to load
        if (loadError != null) {
            throw IOException(
                "Saved data could not be loaded. Changes are blocked."
            )
        }

        try {

            file.parentFile?.mkdirs()

            val json = gson.toJson(updatedMovies)

            val output = atomicFile.startWrite()

            try {

                output.write(json.toByteArray(Charsets.UTF_8))

                atomicFile.finishWrite(output)

            } catch (e: Exception) {

                atomicFile.failWrite(output)

                throw e
            }

            Timber.i("Saved ${updatedMovies.size} movies")

        } catch (e: Exception) {

            Timber.e(e, "Error saving movie data")

            throw IOException("Unable to save movie data.", e)
        }
    }

    // Returns all movies
    fun findAll(): List<Movie> {
        return movies
    }

    // Finds a movie using its ID
    fun findOne(id: Long): Movie? {
        return movies.find { it.id == id }
    }

    // Creates a new movie
    fun create(movie: Movie) {

        movie.validate()

        movie.id = (movies.maxOfOrNull { it.id } ?: 0L) + 1L

        val updatedMovies = ArrayList(movies)

        updatedMovies.add(movie)

        // Save before changing the list in memory
        save(updatedMovies)

        movies.add(movie)
    }

    // Updates an existing movie
    fun update(movie: Movie) {

        movie.validate()

        val index = movies.indexOfFirst { it.id == movie.id }

        if (index == -1) {
            throw IllegalArgumentException("Movie not found")
        }

        val updatedMovies = ArrayList(movies)

        updatedMovies[index] = movie

        // Save before updating the list in memory
        save(updatedMovies)

        movies[index] = movie
    }

    // Deletes a movie using its ID
    fun delete(id: Long) {

        val updatedMovies = ArrayList(movies)

        val deleted = updatedMovies.removeAll { it.id == id }

        if (deleted) {

            // Save before deleting from the list in memory
            save(updatedMovies)

            movies.removeAll { it.id == id }
        }
    }
}
