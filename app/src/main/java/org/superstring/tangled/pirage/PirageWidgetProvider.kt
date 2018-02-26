package org.superstring.tangled.pirage

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import org.jetbrains.anko.intentFor
import timber.log.pirage.warn

/**
 * Created by Brian Parma on 1/27/16.
 *
 * Updates widget.
 */
class PirageWidgetProvider : AppWidgetProvider() {
    companion object {
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
                            PendingIntent.getService(context, 0, context.intentFor<ClickService>(), 0))
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
