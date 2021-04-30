package com.k310.fitness.util

import android.graphics.Color

object Constants {
    const val APP_DB_NAME = "fitness_k310_db"
    const val REQ_CODE_LOCATION_PERMISSIONS = 0
    const val EXTRA_MESSAGE = "com.k310.fitness.MESSAGE"
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val TIMER_UPDATE_INTERVAL = 50L
    const val LOCATION_UPDATE_INTERVAL = 1000L
    const val FASTEST_LOCATION_INTERVAL = 400L

    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 8f
    const val MAP_ZOOM = 15f

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1
}