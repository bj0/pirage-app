package org.superstring.tangled.pirage

import android.app.Application
import android.content.Context

/**
 * Created by Brian Parma on 1/27/16.
 */

class MainApplication : Application() {
    companion object {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        // save context so we can access it in PirageApi
        MainApplication.context = applicationContext
    }
}