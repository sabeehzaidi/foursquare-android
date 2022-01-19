package com.sabeeh.foursquareandroid

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sabeeh.foursquareandroid.data.Repository
import com.sabeeh.foursquareandroid.databinding.FragmentMapsBinding
import com.sabeeh.foursquareandroid.utils.Constants
import com.sabeeh.foursquareandroid.utils.NetworkResult
import com.sabeeh.foursquareandroid.viewmodel.MapsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MapsFragment : Fragment() {

    @Inject
    lateinit var repository: Repository

    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var mapsViewModelFactory: MapsViewModelFactory
    private lateinit var _binding: FragmentMapsBinding
    private var headerAuth = Constants.API_KEY
    private var params = HashMap<String, String>()

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
//                        imageUrl = response.data
//                        _binding.imgDog.load(
//                            response.data.message
//                        ) {
//                            transformations(RoundedCornersTransformation(16f))
//                        }
                        Log.d(Constants.TAG, "Response Successful")
                    }
//                    displayProgressBar(false)
                }

                is NetworkResult.Error -> {
//                    displayProgressBar(false)
                    Toast.makeText(
                        context,
                        response.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(Constants.TAG, "Response Failed ${response.message}")
                }

                is NetworkResult.Loading -> {
//                    displayProgressBar(true)
                }
            }
        }
    }
}