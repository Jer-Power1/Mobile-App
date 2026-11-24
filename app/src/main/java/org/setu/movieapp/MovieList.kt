
package org.setu.movieapp

// Stores the movies and handles CRUD
class MovieList {

    // List that holds all the movies
    private val movies = ArrayList<Movie>()

    // Returns all movies
    fun findAll(): List<Movie> {
        return movies
    }

    // Finds a movie using its ID
    fun findOne(id: Long): Movie? {
        return movies.find { it.id == id }
    }

    // Adds a new movie
    fun create(movie: Movie) {

        // Checks the movie details before adding it
        movie.validate()

        // Gives the new movie an ID
        movie.id = (movies.maxOfOrNull { it.id } ?: 0L) + 1L

        movies.add(movie)
    }

    // Updates an existing movie
    fun update(movie: Movie) {

        // Checks the new details before updating
        movie.validate()

        val foundMovie = findOne(movie.id)

        // Updates the movie if it exists
        if (foundMovie != null) {
            foundMovie.title = movie.title
            foundMovie.description = movie.description
            foundMovie.releaseDay = movie.releaseDay
            foundMovie.releaseMonth = movie.releaseMonth

            // Updates the saved movie image
            foundMovie.imageUri = movie.imageUri
        }
    }

    // Deletes a movie using its ID
    fun delete(id: Long) {
        movies.removeAll { it.id == id }
    }
}
