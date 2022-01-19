package com.sabeeh.foursquareandroid.model.places

import com.google.gson.annotations.SerializedName


data class Circle (

    @SerializedName("center" ) var center : Center? = Center(),
    @SerializedName("radius" ) var radius : Int?    = null

)