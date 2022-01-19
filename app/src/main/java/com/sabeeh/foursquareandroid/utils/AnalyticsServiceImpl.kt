package com.sabeeh.foursquareandroid.utils

import android.util.Log

class AnalyticsServiceImpl : AnalyticsService {

    override fun logEvent(message: String) {
        Log.d(Constants.TAG, message)
    }
}