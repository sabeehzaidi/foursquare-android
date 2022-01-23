package com.sabeeh.foursquareandroid.viewmodel

import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.sabeeh.foursquareandroid.location.LocationManagerImpl
import com.sabeeh.foursquareandroid.data.Repository
import com.sabeeh.foursquareandroid.model.places.PlacesResponse
import com.sabeeh.foursquareandroid.model.places.PlaceDetails
import com.sabeeh.foursquareandroid.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor
    (
    private val repository: Repository
) : ViewModel() {

    private val _response: MutableLiveData<NetworkResult<PlacesResponse>> = MutableLiveData()
    val response: LiveData<NetworkResult<PlacesResponse>> = _response

    private val locationManager = LocationManagerImpl()

    fun fetchPlacesResponse(headerAuth : String, params : Map<String, String>) = viewModelScope.launch {
        repository.getPlaces(headerAuth, params).collect {
            values -> _response.value = values
        }
    }

    fun updateCacheData(results: ArrayList<PlaceDetails>) {
        repository.updateCacheData(results)
    }

    fun getCacheData() : ArrayList<PlaceDetails> {
        return repository.getCacheDataAsList()
    }

    fun getLocation() : LatLng
    {
        return locationManager.getLocation()
    }

}