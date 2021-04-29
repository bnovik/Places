package com.example.places.di

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
            .build()

    @Provides
    fun provideRealm(realmConfiguration: RealmConfiguration): Realm =
        Realm.getInstance(realmConfiguration)

}

