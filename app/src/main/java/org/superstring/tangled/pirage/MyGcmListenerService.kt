package org.superstring.tangled.pirage

import android.app.Notification
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import com.google.android.gms.gcm.GcmListenerService
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.notificationManager
//import java.text.SimpleDateFormat

/**
 * Created by Brian Parma on 1/27/16.
 *
 * Receives GCM message when door state changes.
 */

class MyGcmListenerService : GcmListenerService(), AnkoLogger {
    override fun onMessageReceived(from: String?, data: Bundle?) {
        info("got gcm message: $data")
        val isOpen = data?.getString("mag")?.toBoolean()

        if ( isOpen != null ) {
            // remember new state
            MainApplication.isOpen = isOpen

            // update widget
            PirageWidgetProvider.updateWidgets(this, isOpen)

            // show a notification
            doNotify(isOpen)
        }
    }
}

private val notificationId = 2553

fun Context.doNotify(isOpen: Boolean) {
//    val sdf = SimpleDateFormat("HH:mm:ss")
    val message = "Garage ${if (isOpen) "Open!" else "Closed!"}"
    val largeIcon = BitmapFactory.decodeResource(resources, if ( isOpen) R.drawable.open else R.drawable.closed)

    val notification = Notification.Builder(this)
            .setLargeIcon(largeIcon)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("Pirage!")
            .setContentText(message)
            .setPriority(Notification.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .addAction(
                    if ( isOpen ) R.mipmap.close_icon else R.mipmap.open_icon,
                    if ( isOpen ) "Close" else "Open", PendingIntent.getService(this, 0, intentFor<ClickService>(), 0))

    val stackBuilder = TaskStackBuilder.create(this)
    stackBuilder.addParentStack(MainActivity::class.java)
    stackBuilder.addNextIntent(intentFor<MainActivity>())
    notification.setContentIntent(stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT))

    notificationManager.notify(notificationId, notification.build())
}
