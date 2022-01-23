package com.sabeeh.foursquareandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sabeeh.foursquareandroid.data.RepositoryImpl
import java.lang.IllegalArgumentException

class MapsViewModelFactory(private val repositoryImpl: RepositoryImpl): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(repositoryImpl) as T
        }

        throw IllegalArgumentException("Error creating viewmodel")
    }
}