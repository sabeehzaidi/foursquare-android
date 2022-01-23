package com.sabeeh.foursquareandroid.data

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.sabeeh.foursquareandroid.data.remote.RemoteDataSource
import com.sabeeh.foursquareandroid.model.BaseApiResponse
import com.sabeeh.foursquareandroid.model.places.PlaceDetails
import com.sabeeh.foursquareandroid.model.places.PlacesResponse
import com.sabeeh.foursquareandroid.utils.NetworkResult
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject


@ActivityRetainedScoped
class Repository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {

    var places = MutableLiveData<ArrayList<PlaceDetails>>()
    var cacheData = HashMap<String, PlaceDetails>()
    var mapperArray = ArrayList<PlaceDetails>()

    suspend fun getPlaces(headerAuth : String, params : Map<String, String>): Flow<NetworkResult<PlacesResponse>> {
        return flow<NetworkResult<PlacesResponse>> {
            emit(safeApiCall { remoteDataSource.getPlaces(headerAuth, params) })
        }.flowOn(Dispatchers.IO)
    }

    fun updateCacheData(results: ArrayList<PlaceDetails>) {
        //Add to HashMap if a place by that name doesn't exist already
        for(place in results)
        {
            if(!cacheData.contains(place.name))
            {
                cacheData.put(place.name.toString(), place)
            }
        }
    }

    fun getCacheDataAsList(): ArrayList<PlaceDetails> {

        val values: Collection<PlaceDetails> = cacheData.values
        mapperArray = ArrayList(values)
        return mapperArray
    }

}

