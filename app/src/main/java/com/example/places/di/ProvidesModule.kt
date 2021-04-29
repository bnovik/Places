package com.example.places.di

import com.example.places.data.*
import com.example.places.data.arcgis.PlacesRemoteDataSourceImpl
import com.example.places.data.realm.PlacesLocalDataSourceImpl
import com.example.places.domain.PlacesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import io.realm.Realm
import io.realm.RealmConfiguration

@Module
@InstallIn(ActivityComponent::class)
class ProvidesModule {


    @Provides
    fun provideRealmConfiguration(): RealmConfiguration =
        RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()

    @Provides
    fun provideRealm(realmConfiguration: RealmConfiguration): Realm =
        Realm.getInstance(realmConfiguration)

}



