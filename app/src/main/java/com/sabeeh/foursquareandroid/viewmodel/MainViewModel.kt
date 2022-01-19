package com.sabeeh.foursquareandroid.viewmodel

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import com.sabeeh.foursquareandroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor
    (
    application: Application
) : AndroidViewModel(application) {

  fun replaceFragment(supportFragmentManager: FragmentManager, fragment: Fragment) {
      supportFragmentManager.beginTransaction()
          .replace(R.id.fragmentContainer, fragment)
          .commitNow()
  }

}