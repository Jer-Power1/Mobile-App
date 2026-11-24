
package org.setu.movieapp

class MovieList {

    // Stores all movies in memory
    private val movies = ArrayList<Movie>()

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
        }
    }

    // Deletes a movie using its ID
    fun delete(id: Long) {
        movies.removeAll { it.id == id }
    }
}
