
package org.setu.movieapp

import android.app.Activity
import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Identifies the app when requesting map data
        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_map_picker)

        map = findViewById(R.id.map)

        // Set up the OpenStreetMap map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        // Start the map in Waterford
        val start = GeoPoint(52.245954, -7.139539)

        map.controller.setZoom(18.0)
        map.controller.setCenter(start)

        setSelectedPoint(start)

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

    // Creates or moves the selected location marker
    private fun setSelectedPoint(point: GeoPoint) {

        selectedPoint = point

        if (selectedMarker == null) {

            selectedMarker = Marker(map)

            selectedMarker?.setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )

            map.overlays.add(selectedMarker!!)
        }

        selectedMarker?.position = point
        selectedMarker?.title = "Selected Cinema Location"

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
}
