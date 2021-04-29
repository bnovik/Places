package com.example.places.domain

import com.example.places.domain.models.Place
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.reactivex.Completable
import io.reactivex.Flowable

interface PlacesRepository {

    fun observePlaces(latLng: LatLng, latLngBounds: LatLngBounds): Flowable<List<Place>>

    fun refreshPlaces(latLng: LatLng): Completable

}