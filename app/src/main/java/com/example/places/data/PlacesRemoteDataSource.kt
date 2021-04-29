package com.example.places.data

import com.example.places.domain.models.Place
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single

interface PlacesRemoteDataSource {

    fun fetchPlaces(latLng: LatLng): Single<List<Place>>
}