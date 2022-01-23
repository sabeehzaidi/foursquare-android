package com.sabeeh.foursquareandroid

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sabeeh.foursquareandroid.databinding.ActivityMainBinding
import com.sabeeh.foursquareandroid.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel>()
    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val mapsFragment = MapsFragment()
        replaceFragment(mapsFragment)
    }

    fun replaceFragment(mapsFragment: Fragment) {
        mainViewModel.replaceFragment(supportFragmentManager, mapsFragment)
    }
}
