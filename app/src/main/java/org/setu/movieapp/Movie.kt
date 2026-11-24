
package org.setu.movieapp

// Stores the details for each movie
data class Movie(
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var releaseDay: Double = 0.0,
    var releaseMonth: Double = 0.0,
    var imageUri: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null
) {

    // Checks that the movie details are valid
    fun validate() {

        require(title.isNotBlank()) {
            "Movie title must not be empty."
        }

        require(
            releaseDay.isFinite() &&
                    releaseDay in 1.0..31.0 &&
                    releaseDay % 1.0 == 0.0
        ) {
            "Release day must be between 1 and 31."
        }

        require(
            releaseMonth.isFinite() &&
                    releaseMonth in 1.0..12.0 &&
                    releaseMonth % 1.0 == 0.0
        ) {
            "Release month must be between 1 and 12."
        }

        // Check the cinema location if one has been selected
        val lat = latitude
        val lon = longitude

        if (lat != null || lon != null) {

            require(
                lat != null &&
                        lon != null &&
                        lat.isFinite() &&
                        lon.isFinite() &&
                        lat in -90.0..90.0 &&
                        lon in -180.0..180.0
            ) {
                "Invalid cinema location."
            }
        }
    }
}
