
package org.setu.movieapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(
    private var movies: List<Movie>,
    private val onEdit: (Movie) -> Unit,
    private val onDelete: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val titleText: TextView =
            itemView.findViewById(R.id.titleText)

        val descriptionText: TextView =
            itemView.findViewById(R.id.descriptionText)

        val releaseDateText: TextView =
            itemView.findViewById(R.id.releaseDateText)

        val editButton: Button =
            itemView.findViewById(R.id.editButton)

        val deleteButton: Button =
            itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_movie,
                parent,
                false
            )

        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MovieViewHolder,
        position: Int
    ) {
        val movie = movies[position]

        holder.titleText.text =
            "${movie.id}: ${movie.title}"

        holder.descriptionText.text =
            movie.description

        holder.releaseDateText.text =
            "Release: ${movie.releaseDay.toInt()}/${movie.releaseMonth.toInt()}"

        holder.editButton.setOnClickListener {
            onEdit(movie)
        }

        holder.deleteButton.setOnClickListener {
            onDelete(movie)
        }
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}
