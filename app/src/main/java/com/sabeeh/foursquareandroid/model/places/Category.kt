package com.sabeeh.foursquareandroid.model.places

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id"    ) var id  : Int? = null,
    @SerializedName("name"  ) var name  : String? = null,
    @SerializedName("icon"  ) var icon  : Icon? = null,
)