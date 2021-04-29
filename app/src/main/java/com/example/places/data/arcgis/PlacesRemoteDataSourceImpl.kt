package com.example.places.data.arcgis

import android.os.Build
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters
import com.esri.arcgisruntime.tasks.geocode.LocatorTask
import com.example.places.BuildConfig
import com.example.places.data.PlacesRemoteDataSource
import com.example.places.domain.models.Location
import com.example.places.domain.models.Place
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single
import javax.inject.Inject

class PlacesRemoteDataSourceImpl @Inject constructor(
) : PlacesRemoteDataSource {

    companion object {
        private const val GEOCODE_SERVER_URI =
            "https://geocode-api.arcgis.com/arcgis/rest/services/World/GeocodeServer"
        private const val POI_CATEGORY = "food"
        private const val PLACE_LIMIT = 20
    }


    private val locatorTask by lazy {
        LocatorTask(GEOCODE_SERVER_URI).apply {
            apiKey = BuildConfig.ARCGIS_API_KEY
        }
    }

    override fun fetchPlaces(latLng: LatLng): Single<List<Place>> =
        fetchNearbyFoodPlaces(latLng)
    
    private fun fetchNearbyFoodPlaces(latLng: LatLng): Single<List<Place>> =
        Single.create { emitter ->
            val geocodeParameters = GeocodeParameters()
                .apply {
                    maxResults = PLACE_LIMIT
                    categories.add(POI_CATEGORY)
                    preferredSearchLocation = latLng.toArcGISPoint()
                    resultAttributeNames.add("PlaceName")
                    resultAttributeNames.add("Place_addr")
                }

            val geocodeResultsFuture = locatorTask.geocodeAsync("", geocodeParameters)

            geocodeResultsFuture.addDoneListener {
                try {
                    val geocodeResults = geocodeResultsFuture.get()
                    emitter.onSuccess(geocodeResults.map {
                        Place(
                            location = Location(
                                it.displayLocation.y,
                                it.displayLocation.x
                            ),
                            lable = it.attributes["PlaceName"] as String,
                            address = it.attributes["Place_addr"] as String

                        )
                    })
                } catch (e: Exception) {
                    emitter.onError(e)
                }
            }
        }


    private fun LatLng.toArcGISPoint(): Point =
        Point(longitude, latitude, 0.0)


}