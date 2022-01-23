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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sabeeh.foursquareandroid.data.Repository
import com.sabeeh.foursquareandroid.databinding.FragmentMapsBinding
import com.sabeeh.foursquareandroid.logging.AnalyticsService
import com.sabeeh.foursquareandroid.model.places.PlaceDetails
import com.sabeeh.foursquareandroid.model.places.PlacesResponse
import com.sabeeh.foursquareandroid.utils.Constants
import com.sabeeh.foursquareandroid.utils.NetworkResult
import com.sabeeh.foursquareandroid.viewmodel.MapsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_bottom_sheet.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.bottomSheet
import java.lang.IllegalArgumentException
import javax.inject.Inject

@AndroidEntryPoint
class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var analyticsService : AnalyticsService

    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var mapsViewModelFactory: MapsViewModelFactory
    private lateinit var _binding: FragmentMapsBinding
    private var places = PlacesResponse()
    private lateinit var mMap : GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var headerAuth = Constants.FOURSQUARE_API_KEY
    private var params = HashMap<String, String>()
    private var mapReady = false

    private var prevMarker: Marker? = null

    private val mMapCallback = OnMapReadyCallback { googleMap ->
        mapReady = true
        mMap = googleMap
        animateCameraToLocation(mapsViewModel.getLocation())
        mMap.setOnMarkerClickListener(this)
        mMap.setOnInfoWindowClickListener(this)
        mMap.setOnCameraMoveListener(this)
        mMap.setOnCameraIdleListener(this)
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

        setLocationParamsForApiQuery(mapsViewModel.getLocation())
        fetchDataFromServer(headerAuth, params)
    }

    fun updateMap()
    {
        mMap.clear()
        if(mapReady && places.results.isNotEmpty())
        {
            places.results.forEach() {
                place ->
                if(place.geocodes?.main?.longitude != null && place.geocodes?.main?.latitude != null && isInBounds(place))
                {
                    val marker = mMap.addMarker(MarkerOptions().position(LatLng(place.geocodes?.main?.latitude!!, place.geocodes?.main?.longitude!!)).title(place.name))
                    marker?.tag = place
                }
            }
        }
    }



    private val _defaultDistanceUnit = "m"
    private fun setBottomSheetObserver()
    {
        mapsViewModel.selectedPlace.observe(requireActivity()) { data ->
            placeName.text = data.name
            placeAddress.text = data.location?.address ?: "..."
            placeDistance.text = data.distance.toString().plus(_defaultDistanceUnit).plus(getString(R.string.distance_away))
        }
    }

    private fun setLocationParamsForApiQuery(location: LatLng)
    {
        analyticsService.logEvent("New Map Center: ${location.latitude},${location.longitude}")
        params.put("ll", "${location.latitude},${location.longitude}")
    }

    private fun fetchResponse(headerAuth : String, params : Map<String, String>) {
        mapsViewModel.fetchPlacesResponse(headerAuth, params)
    }

    private fun fetchDataFromServer(headerAuth: String, params : Map<String, String>) {
        fetchResponse(headerAuth, params)
        mapsViewModel.response.observe(requireActivity()) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { _ ->
                        analyticsService.logEvent("Response Successful. ${response.data.results}")
                        mapsViewModel.updateCacheData(response.data.results)
                        this.places.results = mapsViewModel.getCacheData()
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
        mMap.animateCamera(CameraUpdateFactory.zoomIn())
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        analyticsService.logEvent("Marker ${marker.title} tapped")
        setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED)
        mapsViewModel.setSelectedPlace(marker.tag as PlaceDetails)
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

    fun setBottomSheetState(state: Int)
    {
        bottomSheetBehavior.state = state
    }

    override fun onInfoWindowClick(marker: Marker) {
        analyticsService.logEvent("Info for ${marker.title} tapped")
    }

    override fun onCameraMove() {
        analyticsService.logEvent("On Camera Moved")
    }

    override fun onCameraIdle() {
        analyticsService.logEvent("On Camera Move Stopped")
        setLocationParamsForApiQuery(mMap.projection.visibleRegion.latLngBounds.center)
        fetchDataFromCache()
        fetchResponse(headerAuth, params)
        updateMap()
    }

    fun fetchDataFromCache()
    {
        this.places.results = mapsViewModel.getCacheData()
    }

    fun isInBounds(place : PlaceDetails) : Boolean
    {
        val bounds = LatLng(place.geocodes?.main?.latitude!!, place.geocodes?.main?.longitude!!)
        if(mMap.projection.visibleRegion.latLngBounds.contains(bounds))
        {
            return true
        }

        return false
    }

}