package com.sabeeh.foursquareandroid.data.remote

import com.sabeeh.foursquareandroid.model.DogResponse
import com.sabeeh.foursquareandroid.utils.Constants
import retrofit2.Response
import retrofit2.http.GET

interface DogService {

    @GET(Constants.RANDOM_URL)
    suspend fun getDog(): Response<DogResponse>
}
