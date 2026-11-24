
package org.setu.movieapp

// Stores the details for each movie
data class Movie(

    // ID used to identify the movie
    var id: Long = 0,

    // Movie title and description
    var title: String = "",
    var description: String = "",

    // Release date
    var releaseDay: Double = 0.0,
    var releaseMonth: Double = 0.0,

    // Stores the location of the selected image
    var imageUri: String? = null

) {

    // Checks that the movie details are valid
    fun validate() {

        // Makes sure the title is not blank
        require(title.isNotBlank()) {
            "Movie title must not be empty."
        }

        // Checks that the release day is valid
        require(releaseDay.isFinite() && releaseDay in 1.0..31.0
                && releaseDay % 1.0 == 0.0) {
            "Release day must be between 1 and 31."
        }

        // Checks that the release month is valid
        require(releaseMonth.isFinite() && releaseMonth in 1.0..12.0
                && releaseMonth % 1.0 == 0.0) {
            "Release month must be between 1 and 12."
        }
    }
}
