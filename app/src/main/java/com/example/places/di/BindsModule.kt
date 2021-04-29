package com.example.places.di

import com.example.places.data.PlacesLocalDataSource
import com.example.places.data.PlacesRemoteDataSource
import com.example.places.data.PlacesRepositoryImpl
import com.example.places.data.arcgis.PlacesRemoteDataSourceImpl
import com.example.places.data.realm.PlacesLocalDataSourceImpl
import com.example.places.domain.PlacesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class BindsModule {

    @Binds
    abstract fun contributePlacesLocalDataSource(placesLocalDataSourceImpl: PlacesLocalDataSourceImpl): PlacesLocalDataSource

    @Binds
    abstract fun contributePlacesRemoteDataSource(placesRemoteDataSourceImpl: PlacesRemoteDataSourceImpl): PlacesRemoteDataSource

    @Binds
    abstract fun contributePlacesRepository(placesRepositoryImpl: PlacesRepositoryImpl): PlacesRepository
}