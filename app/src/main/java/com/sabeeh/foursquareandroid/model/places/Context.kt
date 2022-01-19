package com.sabeeh.foursquareandroid.model.places

import com.google.gson.annotations.SerializedName


data class Context (

  @SerializedName("geo_bounds" ) var geoBounds : GeoBounds? = GeoBounds()

)