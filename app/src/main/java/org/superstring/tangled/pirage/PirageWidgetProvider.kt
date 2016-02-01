package org.superstring.tangled.pirage

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import org.jetbrains.anko.*

/**
 * Created by Brian Parma on 1/27/16.
 */
class PirageWidgetProvider : AppWidgetProvider(), AnkoLogger {
    companion object : AnkoLogger {
        // update the widget, called from monitor service
        fun updateWidgets(context: Context, isOpen: Boolean) {

            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, PirageWidgetProvider::class.java))

            for( id in ids) {
                val pendingIntent = PendingIntent.getService(context, 0, context.intentFor<ClickService>(), 0)
                val remoteViews = RemoteViews(context.packageName, R.layout.widget);

                warn("update widget: $isOpen")
                remoteViews.setImageViewResource(R.id.button, if(isOpen) R.drawable.open else R.drawable.closed)
                remoteViews.setOnClickPendingIntent(R.id.button, pendingIntent)
                manager.updateAppWidget(id, remoteViews)
            }
        }

    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        // sent intent to service (it starts and refreshes)
        warn("onupdate: $context")
        context?.startService<PirageMonitorService>("update-widget" to true)
    }

}
