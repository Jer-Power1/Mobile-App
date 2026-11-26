
package org.setu.movieapp

import android.app.Application
import android.widget.Toast
import timber.log.Timber

class MovieApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Set up Timber logging
        if (Timber.treeCount == 0) {
            Timber.plant(Timber.DebugTree())
        }

        // Load saved movies when the application starts
        AppData.initialize(this)

        // Inform the user if saved data could not be loaded
        AppData.movies.loadError?.let { message ->

            Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
            ).show()
        }

        Timber.i("Movie App initialised")
    }
}
