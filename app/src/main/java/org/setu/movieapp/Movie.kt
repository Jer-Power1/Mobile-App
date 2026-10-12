
package org.setu.movieapp

data class Movie(
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var releaseDay: Double = 0.0,
    var releaseMonth: Double = 0.0
)
