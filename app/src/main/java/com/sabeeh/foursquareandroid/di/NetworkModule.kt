package com.sabeeh.foursquareandroid.di

import com.sabeeh.foursquareandroid.data.Repository
import com.sabeeh.foursquareandroid.data.remote.PlacesApiService
import com.sabeeh.foursquareandroid.data.remote.RemoteDataSource
import com.sabeeh.foursquareandroid.utils.Constants.Companion.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory =
         GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun providePlacesApiService(retrofit: Retrofit): PlacesApiService =
        retrofit.create(PlacesApiService::class.java)

    @Singleton
    @Provides
    fun provideRepository(remoteDataSource : RemoteDataSource) : Repository =
        Repository(remoteDataSource)

    @Singleton
    @Provides
    fun provideRemoteDataSource(placesApiService: PlacesApiService) : RemoteDataSource =
        RemoteDataSource(placesApiService)
}