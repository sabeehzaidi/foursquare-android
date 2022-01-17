package com.sabeeh.foursquareandroid.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sabeeh.foursquareandroid.data.Repository
import com.sabeeh.foursquareandroid.model.PlacesResponse
import com.sabeeh.foursquareandroid.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor
    (
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _response: MutableLiveData<NetworkResult<PlacesResponse>> = MutableLiveData()
    val response: LiveData<NetworkResult<PlacesResponse>> = _response

    private val _downloadResponse: MutableLiveData<Boolean> = MutableLiveData()
    val downloadResponse = _downloadResponse

    fun fetchPlacesResponse(headerAuth : String, params : Map<String, String>) = viewModelScope.launch {
        repository.getPlaces(headerAuth, params).collect { values ->
            _response.value = values
        }
    }


    fun downloadImage(bitmap: Bitmap, dir: File, fileName: String) {

        viewModelScope.launch {
            repository.saveImage(bitmap, dir, fileName).collect { value ->
                _downloadResponse.value = value
            }
        }
    }


    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}