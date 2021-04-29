package com.example.places.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.places.BuildConfig
import com.example.places.R
import com.example.places.domain.models.Place
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


@AndroidEntryPoint
class MapsActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34
        private const val INITIAL_CAMERA_ZOOM = 14.0f
    }


    @Inject
    lateinit var viewModel: MapsViewModel

    // The Fused Location Provider provides access to location APIs.
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(applicationContext)
    }

    // Allows class to cancel the location request if it exits the activity.
    // Typically, you use one cancellation source per lifecycle.
    private var cancellationTokenSource = CancellationTokenSource()

    // If the user denied a previous permission request, but didn't check "Don't ask again", this
    // Snackbar provides an explanation for why user should approve, i.e., the additional rationale.
    private val fineLocationRationalSnackbar by lazy {
        Snackbar.make(
            this.findViewById(R.id.map),
            R.string.fine_location_permission_rationale,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.ok) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private lateinit var mMap: GoogleMap

    private val isMapCameraInUse: AtomicBoolean = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)

        setupGoogleMap()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        findViewById<FloatingActionButton>(R.id.floatingButton).setOnClickListener {
            supportFragmentManager.beginTransaction().addToBackStack(null)
                .add(R.id.frameLayout, PlacesDetailFragment())
                .commit()
        }
    }

    private fun setupGoogleMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap

            mMap.setOnMarkerClickListenerWithoutAutoCenter()

            requestCurrentLocation()
        }
    }

    private fun requestCurrentLocation() {
        requestLocation { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, INITIAL_CAMERA_ZOOM)
            mMap.animateCamera(newLatLngZoom, object : GoogleMap.CancelableCallback {
                override fun onFinish() {
                    val latLngBounds = mMap.projection.visibleRegion.latLngBounds
                    viewModel.placesViewData.observe(this@MapsActivity) {
                        updateMarkers(it)
                    }
                    viewModel.observePlaces(latLng, latLngBounds)
                }

                override fun onCancel() {}

            })

            mMap.setOnCameraMoveStartedListener {
                isMapCameraInUse.set(true)
            }

            mMap.setOnCameraIdleListener {
                if (isMapCameraInUse.get()) {
                    viewModel.refreshPlaces(mMap.cameraPosition.target)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Cancels location request (if in flight).
        cancellationTokenSource.cancel()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() ->
                    // If user interaction was interrupted, the permission request
                    // is cancelled and you receive an empty array.
                    Log.d(TAG, "User interaction was cancelled.")

                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    Snackbar.make(
                        this.findViewById(R.id.map),
                        R.string.permission_approved_explanation,
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                    requestCurrentLocation()
                }

                else -> {
                    Snackbar.make(
                        this.findViewById(R.id.map),
                        R.string.fine_permission_denied_explanation,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation(locationCallback: (Location) -> Unit) {

        if (applicationContext.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {

            val currentLocationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )

            currentLocationTask.addOnCompleteListener { task: Task<Location> ->
                if (task.isSuccessful && task.result != null) {
                    locationCallback.invoke(task.result)
                }
            }

        } else {
            requestPermissionWithRationale(
                Manifest.permission.ACCESS_FINE_LOCATION,
                REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE,
                fineLocationRationalSnackbar
            )
        }
    }

    private fun updateMarkers(list: List<Place>) {
        mMap.clear()
        list.forEach {
            val marker = MarkerOptions()
                .position(LatLng(it.location.latitude, it.location.longitude))
                .title(it.name)

            mMap.addMarker(marker)
        }
    }

    private fun GoogleMap.setOnMarkerClickListenerWithoutAutoCenter() {
        var lastOpened: Marker? = null

        setOnMarkerClickListener(OnMarkerClickListener { marker -> // Check if there is an open info window
            if (lastOpened != null) {
                // Close the info window
                lastOpened!!.hideInfoWindow()

                // Is the marker the same marker that was already open
                if (lastOpened == marker) {
                    // Nullify the lastOpenned object
                    lastOpened = null
                    // Return so that the info window isn't openned again
                    return@OnMarkerClickListener true
                }
            }

            // Open the info window for the marker
            marker.showInfoWindow()
            // Re-assign the last openned such that we can close it later
            lastOpened = marker

            // Event was handled by our code do not launch default behaviour.
            true
        })
    }

    /**
     * Helper functions to simplify permission checks/requests.
     */
    private fun Context.hasPermission(permission: String): Boolean {

        // Background permissions didn't exit prior to Q, so it's approved by default.
        if (permission == Manifest.permission.ACCESS_BACKGROUND_LOCATION &&
            android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q
        ) {
            return true
        }

        return ActivityCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun Activity.requestPermissionWithRationale(
        permission: String,
        requestCode: Int,
        snackbar: Snackbar
    ) {
        val provideRationale = shouldShowRequestPermissionRationale(permission)

        if (provideRationale) {
            snackbar.show()
        } else {
            requestPermissions(arrayOf(permission), requestCode)
        }
    }
}


