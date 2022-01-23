package com.sabeeh.foursquareandroid

import androidx.lifecycle.MutableLiveData
import com.sabeeh.foursquareandroid.data.IRepository
import com.sabeeh.foursquareandroid.model.places.PlaceDetails
import com.sabeeh.foursquareandroid.model.places.PlacesResponse
import com.sabeeh.foursquareandroid.utils.NetworkResult
import kotlinx.coroutines.flow.Flow


class FakeRepository : IRepository {

    private val places = mutableListOf<PlaceDetails>()

    private val observablePlaces = MutableLiveData<List<PlaceDetails>>(places)

    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun getPlaces(
        headerAuth: String,
        params: Map<String, String>
    ): Flow<NetworkResult<PlacesResponse>> {
        TODO("Not implemented")
    }

    private fun refreshLiveData()
    {
        observablePlaces.postValue(places)
    }

    override fun updateCacheData(results: ArrayList<PlaceDetails>) {
        
    }

    override fun getCacheDataAsList(): ArrayList<PlaceDetails> {
        val fakeList = ArrayList<PlaceDetails>()

        val fake = PlaceDetails()
        fake.name = "Fake Place"
        fake.geocodes?.main?.latitude = 34.03
        fake.geocodes?.main?.longitude = 74.0
        fakeList.add(fake)

        return fakeList
    }
}