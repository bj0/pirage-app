package org.superstring.tangled.pirage

import android.app.Notification
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import com.google.android.gms.gcm.GcmListenerService
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.notificationManager

/**
 * Created by Brian Parma on 1/27/16.
 *
 * Receives GCM message when door state changes.
 */

class MyGcmListenerService: GcmListenerService(), AnkoLogger {
    override fun onMessageReceived(from: String?, data: Bundle?) {
        info("got gcm message: $data")
        val isOpen = data?.getString("mag")?.toBoolean()

        if( isOpen != null ) {
            // remember new state
            MainApplication.isOpen = isOpen

            // update widget
            PirageWidgetProvider.updateWidgets(this, isOpen)

            // show a notification
            doNotify(isOpen)
        }
    }

    private val notificationId = 2553

    private fun doNotify(isOpen: Boolean) {
        val message = "Garage ${if (isOpen) "Open!" else "Closed!"}"
        val largeIcon = BitmapFactory.decodeResource(resources, if( isOpen) R.drawable.open else R.drawable.closed )

        val notification = Notification.Builder(this)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Pirage!")
                .setContentText(message)
                .setPriority(Notification.PRIORITY_HIGH).build()

        notificationManager.notify(notificationId, notification)
    }
}