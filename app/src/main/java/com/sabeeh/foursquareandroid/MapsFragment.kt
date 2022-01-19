package com.sabeeh.foursquareandroid

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sabeeh.foursquareandroid.data.Repository
import com.sabeeh.foursquareandroid.databinding.FragmentMapsBinding
import com.sabeeh.foursquareandroid.model.places.Results
import com.sabeeh.foursquareandroid.logging.AnalyticsService
import com.sabeeh.foursquareandroid.utils.Constants
import com.sabeeh.foursquareandroid.utils.NetworkResult
import com.sabeeh.foursquareandroid.viewmodel.MapsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MapsFragment : Fragment() {

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var analyticsService : AnalyticsService

    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var mapsViewModelFactory: MapsViewModelFactory
    private lateinit var _binding: FragmentMapsBinding
    private var headerAuth = Constants.API_KEY
    private var params = HashMap<String, String>()
    private lateinit var mMap : GoogleMap

    private val callback = OnMapReadyCallback { googleMap ->
        val defaultLocation = LatLng(40.732574046009255,-74.00513697311271)
        mMap = googleMap
        mMap.addMarker(MarkerOptions().position(defaultLocation).title("Marker"))
        animateCameraToLocation(defaultLocation)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        mapsViewModelFactory = MapsViewModelFactory(repository)
        mapsViewModel = ViewModelProvider(this, mapsViewModelFactory).get(MapsViewModel::class.java)

        initGetPlacesParams()
        fetchData(headerAuth, params)
    }

    private fun initGetPlacesParams()
    {
        params.put("ll", "40.732574046009255,-74.00513697311271")
    }

    private fun fetchResponse(headerAuth : String, params : Map<String, String>) {
        mapsViewModel.fetchPlacesResponse(headerAuth, params)
    }

    private fun fetchData(headerAuth: String, params : Map<String, String>) {
        fetchResponse(headerAuth, params)
        mapsViewModel.response.observe(requireActivity()) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        analyticsService.logEvent("Response Successful. ${response.data.results}")
                        renderPlacesOnMap(response.data.results, mMap)
                    }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(
                        context,
                        response.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    analyticsService.logEvent("Response Failed")

                }

                is NetworkResult.Loading -> {
                    analyticsService.logEvent("Loading ...")
                }
            }
        }
    }

    private fun renderPlacesOnMap(placesList: ArrayList<Results>, map: GoogleMap)
    {
        for(place in placesList) {
            mMap.addMarker(MarkerOptions().position(LatLng(place.geocodes?.main?.latitude!!, place.geocodes?.main?.longitude!!)).title(place.name))
        }
    }

    private fun animateCameraToLocation(currentLocation: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn())
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)
    }
}