package com.sabeeh.foursquareandroid.viewmodel

import androidx.lifecycle.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sabeeh.foursquareandroid.data.IRepository
import com.sabeeh.foursquareandroid.location.LocationManagerImpl
import com.sabeeh.foursquareandroid.model.places.PlacesResponse
import com.sabeeh.foursquareandroid.model.places.PlaceDetails
import com.sabeeh.foursquareandroid.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor
    (
    private val repository: IRepository
) : ViewModel() {

    val mMarkers = HashMap<String, Marker>()
    var places = PlacesResponse()
    var mapReady = false
    var prevMarker: Marker? = null

    private val _response: MutableLiveData<NetworkResult<PlacesResponse>> = MutableLiveData()
    val response: LiveData<NetworkResult<PlacesResponse>> = _response

    private val _selectedPlace: MutableLiveData<PlaceDetails> = MutableLiveData()
    val selectedPlace: LiveData<PlaceDetails> = _selectedPlace

    private val locationManager = LocationManagerImpl()

    fun fetchPlacesResponse(headerAuth : String, params : Map<String, String>) = viewModelScope.launch {
        repository.getPlaces(headerAuth, params).collect {
            values -> _response.value = values
        }
    }

    fun setSelectedPlace(place: PlaceDetails?)
    {
        _selectedPlace.value = place
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

    fun clearMapMarkersExceptSelected() {
        mMarkers.forEach() {
            if(!it.value.title.equals(selectedPlace.value?.name))
            {
                it.value.remove()
            }
        }
    }

    fun updateMap(mMap : GoogleMap) {
        if(mapReady && places.results.isNotEmpty())
        {
            places.results.forEach() {
                    place ->
                if(place.geocodes?.main?.longitude != null && place.geocodes?.main?.latitude != null && isInBounds(place, mMap))
                {
                    if(!place.name.equals(selectedPlace.value?.name))
                    {
                        val marker = mMap.addMarker(MarkerOptions().position(LatLng(place.geocodes?.main?.latitude!!, place.geocodes?.main?.longitude!!)).title(place.name))
                        marker?.tag = place
                        mMarkers.put(place.name.toString(), marker!!)
                    }

                }
            }
        }
    }

    fun onMarkerClicked(marker: Marker): Boolean {
        setSelectedPlace(marker.tag as PlaceDetails)
        if(prevMarker != null)
        {
            try {
                prevMarker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
            catch (ex: IllegalArgumentException)
            {
                ex.printStackTrace()
            }
        }
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        prevMarker = marker
        return true
    }

    fun isInBounds(place : PlaceDetails, mMap: GoogleMap) : Boolean
    {
        val bounds = LatLng(place.geocodes?.main?.latitude!!, place.geocodes?.main?.longitude!!)
        if(mMap.projection.visibleRegion.latLngBounds.contains(bounds))
        {
            return true
        }

        return false
    }

}