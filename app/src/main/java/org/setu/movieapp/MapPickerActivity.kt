
package org.setu.movieapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class MapPickerActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private lateinit var selectedPoint: GeoPoint
    private var selectedMarker: Marker? = null

    private var movieTitle = ""
    private var editingMovieId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Identifies the app when requesting map data
        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_map_picker)

        map = findViewById(R.id.map)

        // Set up the OpenStreetMap map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        // Receive the current movie details
        movieTitle = intent.getStringExtra("movieTitle") ?: ""

        editingMovieId = intent.getLongExtra("movieId", -1L)

        // Receive the previously selected coordinates
        val latitude =
            intent.getDoubleExtra("latitude", Double.NaN)

        val longitude =
            intent.getDoubleExtra("longitude", Double.NaN)

        // Use the saved location or start in Waterford
        val start = if (
            latitude.isFinite() &&
            longitude.isFinite() &&
            latitude in -90.0..90.0 &&
            longitude in -180.0..180.0
        ) {
            GeoPoint(latitude, longitude)
        } else {
            GeoPoint(52.245954, -7.139539)
        }

        map.controller.setZoom(18.0)
        map.controller.setCenter(start)

        // Allows the user to select a location on the map
        val receiver = object : MapEventsReceiver {

            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                setSelectedPoint(p)
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                setSelectedPoint(p)
                return true
            }
        }

        map.overlays.add(MapEventsOverlay(receiver))

        // Display previously saved movie locations
        showExistingMarkers()

        // Display the selected location marker
        setSelectedPoint(start)

        val confirmButton =
            findViewById<Button>(R.id.confirmLocationButton)

        // Return the selected coordinates to the movie form
        confirmButton.setOnClickListener {

            val result = Intent()

            result.putExtra("latitude", selectedPoint.latitude)
            result.putExtra("longitude", selectedPoint.longitude)

            setResult(Activity.RESULT_OK, result)

            finish()
        }
    }

    // Displays markers for saved movie locations
    private fun showExistingMarkers() {

        val movies = AppData.movies.findAll()

        for (movie in movies) {

            // Skip the movie currently being edited
            if (movie.id == editingMovieId) {
                continue
            }

            val latitude = movie.latitude
            val longitude = movie.longitude

            // Only display movies with a saved location
            if (latitude != null && longitude != null) {

                val marker = Marker(map)

                marker.position = GeoPoint(
                    latitude,
                    longitude
                )

                marker.title = movie.title
                marker.snippet = movie.description

                marker.setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
                )

                // Saved movie locations use blue markers
                marker.icon?.let { icon ->
                    marker.icon = icon.mutate().apply {
                        setTint(Color.parseColor("#2196F3"))
                    }
                }

                map.overlays.add(marker)
            }
        }

        map.invalidate()
    }

    // Creates or moves the selected location marker
    private fun setSelectedPoint(point: GeoPoint) {

        selectedPoint = point

        if (selectedMarker == null) {

            val marker = Marker(map)

            marker.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )

            // The selected location uses a purple marker
            marker.icon?.let { icon ->
                marker.icon = icon.mutate().apply {
                    setTint(Color.parseColor("#7856FF"))
                }
            }

            selectedMarker = marker

            map.overlays.add(marker)
        }

        selectedMarker?.closeInfoWindow()

        selectedMarker?.position = point

        selectedMarker?.title = movieTitle.ifBlank {
            "Selected Cinema Location"
        }

        selectedMarker?.snippet = "Selected Cinema Location"

        map.invalidate()
    }

    // Resume the map when the activity becomes visible
    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    // Pause the map when leaving the activity
    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    // Release map resources when closing the activity
    override fun onDestroy() {
        map.onDetach()
        super.onDestroy()
    }
}
