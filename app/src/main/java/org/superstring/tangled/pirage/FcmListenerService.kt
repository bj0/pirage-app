package org.superstring.tangled.pirage

import android.app.*
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.notificationManager
import timber.log.pirage.info

/**
 * Created by Brian Parma on 1/27/16.
 *
 * Receives FCM message when door state changes.
 */
class FcmListenerService : FirebaseMessagingService() {
    override fun onCreate() {
        super.onCreate()
        // post 8.0, notifications require a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Door",
                    NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notifications for door open/close"
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val isOpen = message.data?.get("mag")?.toBoolean()
        info { "got fcm message from: $isOpen" }

        if (isOpen != null) {
            // remember new state
            MainApplication.isOpen = isOpen

            // update widget
            PirageWidgetProvider.updateWidgets(this, isOpen)

            // show a notification
            try {
                doNotify(isOpen)
                info { "notify sent" }
            } catch (e: Exception) {
                info { "err:${e.message}" }
            }
        }
    }
}

private const val notificationId = 2553
private const val channelId = "pirage.door"

/**
 * display a notification about state of garage
 */
fun Context.doNotify(isOpen: Boolean) {
    val message = "Garage ${if (isOpen) "Open!" else "Closed!"}"
    val largeIcon = BitmapFactory.decodeResource(resources, if (isOpen) R.drawable.open else R.drawable.closed)

    val notification = NotificationCompat.Builder(this, channelId)
            .setLargeIcon(largeIcon)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("Pirage!")
            .setContentText(message)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .addAction(
                    if (isOpen) R.mipmap.close_icon else R.mipmap.open_icon,
                    if (isOpen) "Close" else "Open", PendingIntent.getService(this, 0, intentFor<ClickService>(), 0))

    val stackBuilder = TaskStackBuilder.create(this)
    stackBuilder.addParentStack(MainActivity::class.java)
    stackBuilder.addNextIntent(intentFor<MainActivity>())
    notification.setContentIntent(stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT))

    notificationManager.notify(notificationId, notification.build())
}
