package com.sabeeh.foursquareandroid.viewmodel

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sabeeh.foursquareandroid.R
import com.sabeeh.foursquareandroid.model.places.PlaceDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor
    (
    application: Application
) : AndroidViewModel(application) {

    private val _selectedPlace: MutableLiveData<PlaceDetails> = MutableLiveData()
    val selectedPlace: LiveData<PlaceDetails> = _selectedPlace

    fun setSelectedPlace(place: PlaceDetails?)
    {
        _selectedPlace.value = place
    }

    fun replaceFragment(supportFragmentManager: FragmentManager, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
        .addToBackStack(fragment.javaClass.name)
        .replace(R.id.fragmentContainer, fragment)
        .commit()
    }

}