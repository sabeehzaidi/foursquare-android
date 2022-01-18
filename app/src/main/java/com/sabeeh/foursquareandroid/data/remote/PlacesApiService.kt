package com.sabeeh.foursquareandroid.data.remote

import com.sabeeh.foursquareandroid.model.PlacesResponse
import com.sabeeh.foursquareandroid.utils.Constants
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.QueryMap

interface PlacesApiService {

    @GET(Constants.PLACES_SEARCH)
    suspend fun getPlaces(@Header("Authorization") auth : String, @QueryMap params : Map<String,String>): Response<PlacesResponse>
}
