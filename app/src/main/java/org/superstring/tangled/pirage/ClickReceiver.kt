package org.superstring.tangled.pirage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import timber.log.pirage.info

/**
 * Created by bjp on 3/14/18.
 */
class ClickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == PirageWidgetProvider.HACK) {
            info { "so hacky" }
            JobIntentService.enqueueWork(context, ClickJob::class.java, 100, Intent())
        }
    }
}