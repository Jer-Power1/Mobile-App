
package org.setu.movieapp

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Connects the movie list to the RecyclerView
class MovieAdapter(
    private var movies: List<Movie>,
    private val listener: OnMovieListener
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    // Holds the views for each movie card
    class MovieViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val titleText: TextView =
            itemView.findViewById(R.id.titleText)

        val descriptionText: TextView =
            itemView.findViewById(R.id.descriptionText)

        val releaseDateText: TextView =
            itemView.findViewById(R.id.releaseDateText)

        // Gets the movie image from the layout
        val movieImage: ImageView =
            itemView.findViewById(R.id.movieImage)
    }

    // Creates a new movie card
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)

        return MovieViewHolder(view)
    }

    // Fills the movie card with the movie details
    override fun onBindViewHolder(
        holder: MovieViewHolder,
        position: Int
    ) {

        val movie = movies[position]

        // Displays the movie details
        holder.titleText.text = movie.title
        holder.descriptionText.text = movie.description

        holder.releaseDateText.text =
            "${movie.releaseDay.toInt()}/${movie.releaseMonth.toInt()}"

        // Converts the saved image string into a URI
        val imageUri = movie.imageUri?.let {
            Uri.parse(it)
        }

        // Displays the selected movie poster
        if (imageUri != null) {

            holder.movieImage.setImageURI(imageUri)

            // Shows the default image if the poster cannot be loaded
            if (holder.movieImage.drawable == null) {
                holder.movieImage.setImageResource(
                    android.R.drawable.ic_menu_gallery
                )
            }

        } else {

            // Shows the default image if no poster was selected
            holder.movieImage.setImageResource(
                android.R.drawable.ic_menu_gallery
            )
        }

        // Opens the movie when clicked
        holder.itemView.setOnClickListener {

            val currentPosition = holder.bindingAdapterPosition

            if (currentPosition != RecyclerView.NO_POSITION) {
                listener.onMovieClick(movies[currentPosition])
            }
        }

        // Handles deleting a movie when long pressed
        holder.itemView.setOnLongClickListener {

            val currentPosition = holder.bindingAdapterPosition

            if (currentPosition != RecyclerView.NO_POSITION) {
                listener.onMovieLongClick(movies[currentPosition])
            } else {
                false
            }
        }
    }

    // Returns the number of movies in the list
    override fun getItemCount(): Int {
        return movies.size
    }

    // Updates the list when movies are changed or filtered
    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}
