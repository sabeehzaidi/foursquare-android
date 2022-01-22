package com.sabeeh.foursquareandroid

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sabeeh.foursquareandroid.data.Repository
import com.sabeeh.foursquareandroid.databinding.FragmentMapsBinding
import com.sabeeh.foursquareandroid.logging.AnalyticsService
import com.sabeeh.foursquareandroid.model.places.PlacesResponse
import com.sabeeh.foursquareandroid.utils.Constants
import com.sabeeh.foursquareandroid.utils.NetworkResult
import com.sabeeh.foursquareandroid.viewmodel.MapsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_bottom_sheet.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.bottomSheet
import javax.inject.Inject

@AndroidEntryPoint
class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var analyticsService : AnalyticsService

    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var mapsViewModelFactory: MapsViewModelFactory
    private lateinit var _binding: FragmentMapsBinding
    private lateinit var places: PlacesResponse
    private lateinit var mMap : GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var headerAuth = Constants.FOURSQUARE_API_KEY
    private var params = HashMap<String, String>()
    private var mapReady = false
    val defaultLocation = LatLng(40.732574046009255,-74.00513697311271)

    private val mMapCallback = OnMapReadyCallback { googleMap ->
        mapReady = true
        mMap = googleMap
        animateCameraToLocation(defaultLocation)
        mMap.setOnMarkerClickListener(this)
        mMap.setOnInfoWindowClickListener(this)
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

        mapsViewModelFactory = MapsViewModelFactory(repository)
        mapsViewModel = ViewModelProvider(this, mapsViewModelFactory).get(MapsViewModel::class.java)
        mapFragment?.getMapAsync(mMapCallback)
        setBottomSheetObserver()

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                analyticsService.logEvent("New State: $newState")
            }
        })

        initGetPlacesParams(defaultLocation)
        fetchData(headerAuth, params)
    }

    fun updateMap()
    {
        if(mapReady && places.results.isNotEmpty())
        {
            places.results.forEach() {
                place ->
                if(place.geocodes?.main?.longitude != null && place?.geocodes?.main?.latitude != null)
                {
                    mMap.addMarker(MarkerOptions().position(LatLng(place.geocodes?.main?.latitude!!, place.geocodes?.main?.longitude!!)).title(place.name))
                }
            }
        }
    }

    private fun initGetPlacesParams(defaultLocation: LatLng)
    {
        params.put("ll", "${defaultLocation.latitude},${defaultLocation.longitude}")
    }

    private fun fetchResponse(headerAuth : String, params : Map<String, String>) {
        mapsViewModel.fetchPlacesResponse(headerAuth, params)
    }

    private val _defaultDistanceUnit = "m"
    private fun setBottomSheetObserver()
    {
        mapsViewModel.selectedPlace.observe(requireActivity()) { data ->
            venueName.text = data.name
            venueAddress.text = data.location?.address ?: "..."
            venueDistance.text = data.distance.toString().plus(_defaultDistanceUnit).plus(getString(R.string.distance_away))
        }
    }

    private fun fetchData(headerAuth: String, params : Map<String, String>) {
        fetchResponse(headerAuth, params)
        mapsViewModel.response.observe(requireActivity()) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        it -> this.places = it
                        analyticsService.logEvent("Response Successful. ${response.data.results}")
                        updateMap()
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

    private fun animateCameraToLocation(currentLocation: LatLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn())
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        analyticsService.logEvent("Marker ${marker.title} tapped")
        setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED)
        mapsViewModel.setSelectedPlace(places.results.find { it.name == marker.title })
        return false
    }

    fun setBottomSheetState(state: Int)
    {
        bottomSheetBehavior.state = state
    }

    override fun onInfoWindowClick(marker: Marker) {
        analyticsService.logEvent("Info for ${marker.title} tapped")
    }


}