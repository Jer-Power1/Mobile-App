
package org.setu.movieapp

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class MovieList(private val file: File) {

    // Stores all movies in memory
    private val movies = ArrayList<Movie>()

    // Converts movie objects to and from JSON
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    // Load saved movies when the list is created
    init {
        load()
    }

    // Loads movies from the JSON file
    private fun load() {

        if (!file.exists()) {
            return
        }

        val type = object : TypeToken<ArrayList<Movie>>() {}.type

        val loaded: ArrayList<Movie> =
            gson.fromJson(file.readText(), type)
                ?: arrayListOf()

        movies.clear()
        movies.addAll(loaded)
    }

    // Saves movies to the JSON file
    private fun save() {

        file.parentFile?.mkdirs()

        file.writeText(gson.toJson(movies))
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

        movies.add(movie)

        // Save after creating a movie
        save()
    }

    // Updates an existing movie
    fun update(movie: Movie) {

        movie.validate()

        val foundMovie = findOne(movie.id)

        if (foundMovie != null) {

            foundMovie.title = movie.title
            foundMovie.description = movie.description
            foundMovie.releaseDay = movie.releaseDay
            foundMovie.releaseMonth = movie.releaseMonth
            foundMovie.imageUri = movie.imageUri

            // Update the cinema location
            foundMovie.latitude = movie.latitude
            foundMovie.longitude = movie.longitude

            // Save after updating a movie
            save()
        }
    }

    // Deletes a movie using its ID
    fun delete(id: Long) {

        val deleted = movies.removeAll { it.id == id }

        // Save only if a movie was deleted
        if (deleted) {
            save()
        }
    }
}
