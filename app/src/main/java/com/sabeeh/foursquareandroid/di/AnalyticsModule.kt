package com.sabeeh.foursquareandroid.di

import com.sabeeh.foursquareandroid.utils.AnalyticsService
import com.sabeeh.foursquareandroid.utils.AnalyticsServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Singleton
    @Provides
    fun provideAnalyticsClient() : AnalyticsService {
        return AnalyticsServiceImpl()
    }
}