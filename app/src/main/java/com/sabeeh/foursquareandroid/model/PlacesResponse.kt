package com.sabeeh.foursquareandroid.model

import com.google.gson.annotations.SerializedName
import com.sabeeh.foursquareandroid.Context
import com.sabeeh.foursquareandroid.Results

data class PlacesResponse(
    @SerializedName("results" ) var results : ArrayList<Results> = arrayListOf(),
    @SerializedName("context" ) var context : Context?           = Context()
)
