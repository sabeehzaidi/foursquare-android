package com.sabeeh.foursquareandroid.di

import com.sabeeh.foursquareandroid.data.IRepository
import com.sabeeh.foursquareandroid.data.RepositoryImpl
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
    fun provideRepository(remoteDataSource : RemoteDataSource) : IRepository =
        RepositoryImpl(remoteDataSource)

    @Singleton
    @Provides
    fun provideRemoteDataSource(placesApiService: PlacesApiService) : RemoteDataSource =
        RemoteDataSource(placesApiService)
}