package com.sabeeh.foursquareandroid.viewmodel

import com.sabeeh.foursquareandroid.FakeRepository
import org.junit.Before

class MapsViewModelTest {

    private lateinit var viewModel: MapsViewModel

    @Before
    fun setUp()
    {
        viewModel = MapsViewModel(FakeRepository())
    }
}