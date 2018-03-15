package org.superstring.tangled.pirage

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.*
import android.support.v4.app.JobIntentService
import android.widget.RemoteViews
import splitties.init.appCtx
import timber.log.pirage.info
import timber.log.pirage.warn

/**
 * Created by Brian Parma on 1/27/16.
 *
 * Updates widget.
 */
class PirageWidgetProvider : AppWidgetProvider() {
    companion object {

        const val HACK = "launch.jobintentservice.hack"

        /**
         * Updates the widget.  Can be called from other services
         */
        fun updateWidgets(context: Context, isOpen: Boolean) {

            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, PirageWidgetProvider::class.java))

            for (id in ids) {
                warn { "update widget: $isOpen" }
                RemoteViews(context.packageName, R.layout.widget).apply {
                    setImageViewResource(R.id.button, if (isOpen) R.drawable.open else R.drawable.closed)
                    setOnClickPendingIntent(R.id.button,
                            PendingIntent.getBroadcast(context, 0,
                                    Intent(appCtx, ClickReceiver::class.java)
                                            .setAction(HACK), 0))
                    manager.updateAppWidget(id, this)
                }
            }
        }
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        // sent intent to service (it starts and refreshes)
        context?.let { updateWidgets(it, MainApplication.isOpen ?: false) }
    }
}
