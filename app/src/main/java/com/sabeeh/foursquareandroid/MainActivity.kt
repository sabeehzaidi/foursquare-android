package com.sabeeh.foursquareandroid

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.load
import coil.request.Disposable
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.sabeeh.foursquareandroid.databinding.ActivityMainBinding
import com.sabeeh.foursquareandroid.utils.Constants
import com.sabeeh.foursquareandroid.utils.NetworkResult
import com.sabeeh.foursquareandroid.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel>()
    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        replaceFragment()
    }

    private fun replaceFragment()
    {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, MapsFragment())
            .commitNow()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
