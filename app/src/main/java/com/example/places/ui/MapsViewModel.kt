package com.example.places.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.places.domain.PlacesRepository
import com.example.places.domain.models.Place
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class MapsViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    private val placesMutableLiveData: MutableLiveData<List<Place>> = MutableLiveData()
    val placesViewData: LiveData<List<Place>> = placesMutableLiveData

    private val compositeDisposable = CompositeDisposable()

    fun observePlaces(latLng: LatLng, latLngBounds: LatLngBounds) {
        placesRepository.observePlaces(latLng, latLngBounds)
            .subscribe {
                placesMutableLiveData.postValue(it)
            }
            .addTo(compositeDisposable)
    }

    fun refreshPlaces(latLng: LatLng) {
        placesRepository.refreshPlaces(latLng)
            .subscribe()
            .addTo(compositeDisposable)
    }

}