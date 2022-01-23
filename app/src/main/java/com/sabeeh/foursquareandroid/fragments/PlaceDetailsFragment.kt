package com.sabeeh.foursquareandroid.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sabeeh.foursquareandroid.databinding.FragmentPlaceDetailsBinding
import com.sabeeh.foursquareandroid.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_place_details.*

class PlaceDetailsFragment : Fragment() {

    private lateinit var _binding: FragmentPlaceDetailsBinding
    private lateinit var mainViewModel: MainViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceDetailsBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        detailsPlaceName.text = mainViewModel.selectedPlace.value?.name
    }
}