package org.superstring.tangled.pirage

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.superstring.tangled.pirage.api.getStatus
import timber.log.Timber
import timber.log.pirage.info

/**
 * Created by Brian Parma on 1/27/16.
 *
 * Stores global variables in companion object:
 * * context - app context used by PirageApi to generate ssl context
 * * isOpen - last known status of door used when widget needs an update
 */

class MainApplication : Application() {
    companion object {
        var isOpen: Boolean? = null
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        // register for fcm
        FirebaseMessaging.getInstance().subscribeToTopic("pirage")

        launch(CommonPool) {
            isOpen = getStatus().open
            info { "status returned: $isOpen" }
            // since getting status takes a while, we might get a true after
            // the widget was initialized with false
            if (isOpen == true)
                PirageWidgetProvider.updateWidgets(applicationContext, true)
        }
    }
}