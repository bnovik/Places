package com.example.places

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm

@HiltAndroidApp
class PlacesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)
    }
}