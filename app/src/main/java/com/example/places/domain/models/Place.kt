package com.example.places.domain.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Place(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var location: Location = Location(),
    var lable: String = "",
    var address: String = "",
) : RealmObject() {

}