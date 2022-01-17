package com.sabeeh.foursquareandroid.model

import com.google.gson.annotations.SerializedName

data class Icon (
    @SerializedName("prefix"  ) var prefix  : String? = null,
    @SerializedName("suffix"  ) var suffix  : String? = null
)