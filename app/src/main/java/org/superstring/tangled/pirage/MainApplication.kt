package org.superstring.tangled.pirage

import android.app.Application
import android.content.Context
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.async
import org.jetbrains.anko.info
import org.jetbrains.anko.startService

/**
 * Created by Brian Parma on 1/27/16.
 *
 * Stores global variables in companion object:
 * * context - app context used by PirageApi to generate ssl context
 * * isOpen - last known status of door used when widget needs an update
 */

class MainApplication : Application(), AnkoLogger {
    companion object {
        lateinit var context: Context
        var isOpen : Boolean? = null
    }

    override fun onCreate() {
        super.onCreate()
        // save context so we can access it in PirageApi
        MainApplication.context = applicationContext

        // register for gcm
        startService<RegisterService>()

        async() {
            isOpen = PirageApi.getStatus().open
            info("status returned: $isOpen")
            // since getting status takes a while, we might get a true after
            // the widget was initialized with false
            if( isOpen ?: false )
                PirageWidgetProvider.updateWidgets(applicationContext, true)
        }
    }
}