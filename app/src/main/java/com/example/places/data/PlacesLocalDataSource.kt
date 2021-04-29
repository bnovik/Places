package com.example.places.data

import com.example.places.domain.models.Place
import io.reactivex.Completable
import io.reactivex.Flowable

interface PlacesLocalDataSource {

    fun observePlaces(): Flowable<List<Place>>

    fun savePlaces(places: List<Place>): Completable
}