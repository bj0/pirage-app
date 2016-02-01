package org.superstring.tangled.pirage

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import org.jetbrains.anko.*

/**
 * Created by Brian Parma on 1/26/16.
 */
class PirageMonitorService : Service(), AnkoLogger {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    val notificationId = 2553

    var isOpen = false

    val msgReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val msg = intent?.getStringExtra("msg")
            info("got msg: $msg")

            isOpen = msg?.toBoolean() ?: false

            PirageWidgetProvider.updateWidgets(this@PirageMonitorService, isOpen)

            this@PirageMonitorService.doNotify("Garage ${if (isOpen) "Open!" else "Closed!"}")
        }
    }

    private fun doNotify(message: String) {
        val largeIcon = BitmapFactory.decodeResource(resources, if( isOpen) R.drawable.open else R.drawable.closed )

        val notification = Notification.Builder(this)
            .setLargeIcon(largeIcon)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("Pirage!")
            .setContentText(message)
            .setPriority(Notification.PRIORITY_HIGH).build()

        notificationManager.notify(notificationId, notification)
    }

    override fun onCreate() {
        super.onCreate()

        //todo setup mqtt
        LocalBroadcastManager.getInstance(this).registerReceiver(msgReceiver, IntentFilter("msg-received"))

        //todo init
        startService<RegisterService>()

        //todo get status
        async() {
            isOpen = PirageApi.getStatus().open
            info("status returned: $isOpen")
        }
    }

    override fun onDestroy() {
        notificationManager.cancel(notificationId)

        LocalBroadcastManager.getInstance(this).unregisterReceiver(msgReceiver)

        // todo clean up mqtt
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        return super.onStartCommand(intent, flags, startId)

        warn("onstartcommand: $intent")

        if( intent != null && intent.getBooleanExtra("update-widget", false) ){
            debug("update call from widget")

            PirageWidgetProvider.updateWidgets(this, isOpen)
        }

        return Service.START_STICKY
    }

}
