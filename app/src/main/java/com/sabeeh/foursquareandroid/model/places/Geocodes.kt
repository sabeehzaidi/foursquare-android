package com.sabeeh.foursquareandroid.model.places

import com.google.gson.annotations.SerializedName


data class Geocodes (

  @SerializedName("main" ) var main : Main? = Main()

)