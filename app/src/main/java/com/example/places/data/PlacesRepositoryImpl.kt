package com.example.places.data

import com.example.places.domain.PlacesRepository
import com.example.places.domain.models.Place
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

class PlacesRepositoryImpl @Inject constructor(
    private val localDataSource: PlacesLocalDataSource,
    private val remoteDataSource: PlacesRemoteDataSource,
) : PlacesRepository {

    override fun observePlaces(latLng: LatLng, latLngBounds: LatLngBounds): Flowable<List<Place>> =
        localDataSource.observePlaces()
            .zipWith(Flowable.range(0, Integer.MAX_VALUE)) { list, index ->
                index to list
            }
            .flatMap { (index, list) ->
                val visiblePlaces = list.filter {
                    latLngBounds.contains(
                        LatLng(
                            it.location.latitude,
                            it.location.longitude
                        )
                    )
                }
                if (index == 0) {
                    if (visiblePlaces.isEmpty()) {
                        return@flatMap refreshPlaces(latLng)
                            .andThen(Flowable.just(0 to visiblePlaces))
                    } else {
                        Flowable.just(index to visiblePlaces)
                    }
                } else {
                    Flowable.just(index to list)
                }
            }
            .map { it.second }


    override fun refreshPlaces(latLng: LatLng): Completable =
        remoteDataSource.fetchPlaces(latLng)
            .flatMapCompletable { localDataSource.savePlaces(it) }
}