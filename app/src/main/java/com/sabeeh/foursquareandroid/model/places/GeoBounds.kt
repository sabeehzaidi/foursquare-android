package com.sabeeh.foursquareandroid.model.places

import com.google.gson.annotations.SerializedName


data class GeoBounds (

  @SerializedName("circle" ) var circle : Circle? = Circle()

)