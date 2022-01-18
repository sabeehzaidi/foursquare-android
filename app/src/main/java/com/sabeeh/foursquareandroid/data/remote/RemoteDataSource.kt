package com.sabeeh.foursquareandroid.data.remote

import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val placesApiService: PlacesApiService) {

    suspend fun getPlaces(headerAuth : String, params : Map<String, String>) =
        placesApiService.getPlaces(headerAuth, params)

}