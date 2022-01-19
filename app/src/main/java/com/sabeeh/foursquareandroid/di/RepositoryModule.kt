package com.sabeeh.foursquareandroid.di

import com.sabeeh.foursquareandroid.data.Repository
import com.sabeeh.foursquareandroid.data.remote.PlacesApiService
import com.sabeeh.foursquareandroid.data.remote.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(remoteDataSource : RemoteDataSource) : Repository =
        Repository(remoteDataSource)

    @Singleton
    @Provides
    fun provideRemoteDataSource(placesApiService: PlacesApiService) : RemoteDataSource =
        RemoteDataSource(placesApiService)
}