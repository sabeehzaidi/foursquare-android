package com.sabeeh.foursquareandroid.data

import com.sabeeh.foursquareandroid.model.places.PlaceDetails
import com.sabeeh.foursquareandroid.model.places.PlacesResponse
import com.sabeeh.foursquareandroid.utils.NetworkResult
import kotlinx.coroutines.flow.Flow

interface IRepository {

    suspend fun getPlaces(headerAuth: String, params: Map<String, String>): Flow<NetworkResult<PlacesResponse>>

    fun updateCacheData(results: ArrayList<PlaceDetails>)

    fun getCacheDataAsList() : ArrayList<PlaceDetails>

}