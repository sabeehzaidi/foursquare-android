package com.sabeeh.foursquareandroid.location

import com.google.android.gms.maps.model.LatLng

interface ILocationManager {

    fun getLocation() : LatLng
}