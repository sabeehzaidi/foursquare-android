package com.sabeeh.foursquareandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sabeeh.foursquareandroid.databinding.FragmentPlaceDetailsBinding

class PlaceDetailsFragment : Fragment() {

    private lateinit var _binding: FragmentPlaceDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceDetailsBinding.inflate(inflater, container, false)
        return _binding.root
    }
}