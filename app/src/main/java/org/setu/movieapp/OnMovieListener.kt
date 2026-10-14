
package org.setu.movieapp

interface OnMovieListener {

    fun onMovieClick(movie: Movie)

    fun onMovieLongClick(movie: Movie): Boolean

}
