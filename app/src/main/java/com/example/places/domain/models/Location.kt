package com.example.places.domain.models

import io.realm.RealmObject

open class Location(
        var latitude: Double = 0.0,
        var longitude: Double = 0.0
) : RealmObject() {

}