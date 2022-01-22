package com.sabeeh.foursquareandroid.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.sabeeh.foursquareandroid.MainActivity
import com.sabeeh.foursquareandroid.MainActivity_GeneratedInjector
import com.sabeeh.foursquareandroid.PlaceDetailsFragment
import com.sabeeh.foursquareandroid.data.Repository
import com.sabeeh.foursquareandroid.model.places.PlacesResponse
import com.sabeeh.foursquareandroid.model.places.PlaceDetails
import com.sabeeh.foursquareandroid.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor
    (
    private val repository: Repository
) : ViewModel() {

    private val _response: MutableLiveData<NetworkResult<PlacesResponse>> = MutableLiveData()
    val response: LiveData<NetworkResult<PlacesResponse>> = _response

    private val _selectedPlace: MutableLiveData<PlaceDetails> = MutableLiveData()
    val selectedPlace: LiveData<PlaceDetails> = _selectedPlace

    fun fetchPlacesResponse(headerAuth : String, params : Map<String, String>) = viewModelScope.launch {
        repository.getPlaces(headerAuth, params).collect {
            values -> _response.value = values
        }
    }

    fun setSelectedPlace(place: PlaceDetails?)
    {
        _selectedPlace.value = place
    }

}