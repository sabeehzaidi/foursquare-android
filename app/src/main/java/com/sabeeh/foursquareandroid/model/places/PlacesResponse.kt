package com.sabeeh.foursquareandroid.model.places

import com.google.gson.annotations.SerializedName

data class PlacesResponse(
    @SerializedName("results" ) var results : ArrayList<Results> = arrayListOf(),
    @SerializedName("context" ) var context : Context?           = Context()
)
