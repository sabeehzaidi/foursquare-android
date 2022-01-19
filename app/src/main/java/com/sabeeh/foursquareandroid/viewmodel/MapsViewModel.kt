package com.sabeeh.foursquareandroid.viewmodel

import androidx.lifecycle.*
import com.sabeeh.foursquareandroid.data.Repository
import com.sabeeh.foursquareandroid.model.PlacesResponse
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

    fun fetchPlacesResponse(headerAuth : String, params : Map<String, String>) = viewModelScope.launch {
        repository.getPlaces(headerAuth, params).collect { values ->
            _response.value = values }
    }

}