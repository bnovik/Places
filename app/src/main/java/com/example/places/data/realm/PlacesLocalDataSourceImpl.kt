package com.example.places.data.realm

import com.example.places.data.PlacesLocalDataSource
import com.example.places.domain.models.Place
import io.reactivex.Completable
import io.reactivex.Flowable
import io.realm.Realm
import javax.inject.Inject

class PlacesLocalDataSourceImpl @Inject constructor(
    private val realm: Realm
) : PlacesLocalDataSource {

    companion object {
        private const val PLACE_LIMIT = 20
    }

    override fun observePlaces(): Flowable<List<Place>> =
        realm.where(Place::class.java).findAllAsync().asFlowable().map { realm.copyFromRealm(it) }
            .skip(1)

    override fun savePlaces(places: List<Place>): Completable = Completable.fromCallable {
        realm.executeTransactionAsync {
            val query = it.where(Place::class.java)
            query.findAll().deleteAllFromRealm()
            it.insertOrUpdate(places.take(PLACE_LIMIT))
        }
    }

}