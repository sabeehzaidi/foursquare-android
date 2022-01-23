package com.sabeeh.foursquareandroid.fragments

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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sabeeh.foursquareandroid.viewmodel.MapsViewModelFactory
import com.sabeeh.foursquareandroid.R
import com.sabeeh.foursquareandroid.data.RepositoryImpl
import com.sabeeh.foursquareandroid.databinding.FragmentMapsBinding
import com.sabeeh.foursquareandroid.logging.AnalyticsService
import com.sabeeh.foursquareandroid.utils.Constants
import com.sabeeh.foursquareandroid.utils.NetworkResult
import com.sabeeh.foursquareandroid.viewmodel.MapsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_bottom_sheet.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.bottomSheet
import javax.inject.Inject

@AndroidEntryPoint
class MapsFragment : Fragment(), GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener {

    @Inject
    lateinit var repositoryImpl: RepositoryImpl

    @Inject
    lateinit var analyticsService : AnalyticsService

    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var mapsViewModelFactory: MapsViewModelFactory
    private lateinit var _binding: FragmentMapsBinding
    private lateinit var mMap : GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    var headerAuth = Constants.FOURSQUARE_API_KEY
    var params = HashMap<String, String>()

    private val mMapCallback = OnMapReadyCallback { googleMap ->
        mapsViewModel.mapReady = true
        mMap = googleMap
        animateCameraToLocation(mapsViewModel.getLocation())
        mMap.setOnMarkerClickListener(this)
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
        mapFragment?.getMapAsync(mMapCallback)

        mapsViewModelFactory = MapsViewModelFactory(repositoryImpl)
        mapsViewModel = ViewModelProvider(this, mapsViewModelFactory).get(MapsViewModel::class.java)

        setSelectedPlaceObserver()
        initBottomSheet()

        setLocationParamsForApiQuery(mapsViewModel.getLocation())
        fetchDataFromServer(headerAuth, params)
    }

    fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        setBottomSheetPreferences()
        setBottomSheetState(BottomSheetBehavior.STATE_COLLAPSED)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                analyticsService.logEvent("New State: $newState")
            }
        })
        btnPlaceDetails.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                setBottomSheetState(BottomSheetBehavior.STATE_EXPANDED)
            }

        })
    }

    fun setBottomSheetPreferences()
    {
        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.halfExpandedRatio = 0.15f
    }

    fun clearMapMarkersExceptSelected()
    {
        mapsViewModel.clearMapMarkersExceptSelected()
    }

    fun updateMap()
    {
        clearMapMarkersExceptSelected()
        mapsViewModel.updateMap(mMap)
    }


    private fun setSelectedPlaceObserver()
    {
        mapsViewModel.selectedPlace.observe(requireActivity()) { data ->
            tvPlaceName.text = data.name
            tvPlaceAddress.text = data.location?.address ?: "..."
            tvPlaceDistance.text = data.distance.toString().plus(Constants.DEFAULT_DISTANCE_UNIT).plus(
                getString(
                    R.string.distance_away
                )
            )
            tvPlaceFullAddress.text = data.location?.address.plus(data.location?.locality).plus(data.location?.country)
            tvPlaceFullName.text = data.name
            tvPlacePostalCode.text = data.location?.postcode
            tvPlaceTimezone.text = data.timezone

            setBottomSheetState(BottomSheetBehavior.STATE_HALF_EXPANDED)
        }
    }

    private fun setLocationParamsForApiQuery(location: LatLng)
    {
        analyticsService.logEvent("New Map Center: ${location.latitude},${location.longitude}")
        params.put("ll", "${location.latitude},${location.longitude}")
        params.put("limit", Constants.PLACES_RESPONSE_LIMIT.toString())
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
                        mapsViewModel.places.results = mapsViewModel.getCacheData()
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
        setBottomSheetState(BottomSheetBehavior.STATE_HALF_EXPANDED)
        return mapsViewModel.onMarkerClicked(marker)
    }

    fun setBottomSheetState(state: Int) {
        bottomSheetBehavior.state = state
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
        mapsViewModel.places.results = mapsViewModel.getCacheData()
    }

}