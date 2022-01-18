package com.sabeeh.foursquareandroid

import com.google.gson.annotations.SerializedName


data class GeoBounds (

  @SerializedName("circle" ) var circle : Circle? = Circle()

)