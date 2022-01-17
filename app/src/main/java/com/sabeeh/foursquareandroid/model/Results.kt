package com.sabeeh.foursquareandroid

import com.google.gson.annotations.SerializedName
import com.sabeeh.foursquareandroid.model.Category


data class Results (

  @SerializedName("fsq_id"         ) var fsqId         : String?           = null,
  @SerializedName("categories"     ) var categories    : ArrayList<Category> = arrayListOf(),
  @SerializedName("chains"         ) var chains        : ArrayList<String> = arrayListOf(),
  @SerializedName("distance"       ) var distance      : Int?              = null,
  @SerializedName("geocodes"       ) var geocodes      : Geocodes?         = Geocodes(),
  @SerializedName("location"       ) var location      : Location?         = Location(),
  @SerializedName("name"           ) var name          : String?           = null,
  @SerializedName("related_places" ) var relatedPlaces : RelatedPlaces?    = RelatedPlaces(),
  @SerializedName("timezone"       ) var timezone      : String?           = null

)