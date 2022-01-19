package com.sabeeh.foursquareandroid.logging

import android.util.Log
import com.sabeeh.foursquareandroid.utils.Constants

class AnalyticsServiceImpl : AnalyticsService {

    override fun logEvent(message: String) {
        Log.d(Constants.TAG, message)
    }
}