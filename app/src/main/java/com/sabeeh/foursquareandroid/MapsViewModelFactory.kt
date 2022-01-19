package com.sabeeh.foursquareandroid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sabeeh.foursquareandroid.data.Repository
import com.sabeeh.foursquareandroid.viewmodel.MapsViewModel
import java.lang.IllegalArgumentException

class MapsViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(repository) as T
        }

        throw IllegalArgumentException("Error creating viewmodel")
    }
}