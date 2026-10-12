
package org.setu.movieapp

class MovieList {

    private val movies = ArrayList<Movie>()

    fun findAll(): List<Movie> {
        return movies
    }

    fun findOne(id: Long): Movie? {
        return movies.find { it.id == id }
    }

    fun create(movie: Movie) {
        movie.id = (movies.maxOfOrNull { it.id } ?: 0L) + 1L
        movies.add(movie)
    }

    fun update(movie: Movie) {
        val foundMovie = findOne(movie.id)

        if (foundMovie != null) {
            foundMovie.title = movie.title
            foundMovie.description = movie.description
            foundMovie.releaseDay = movie.releaseDay
            foundMovie.releaseMonth = movie.releaseMonth
        }
    }

    fun delete(id: Long) {
        movies.removeAll { it.id == id }
    }
}
