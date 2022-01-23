package com.sabeeh.foursquareandroid.location

import com.google.android.gms.maps.model.LatLng

class LocationManagerImpl : ILocationManager {

    private val currentLocation = LatLng(40.732574046009255,-74.00513697311271)

    override fun getLocation(): LatLng {
        return currentLocation
    }

}