package org.superstring.tangled.pirage

import android.app.IntentService
import android.content.Intent
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.onUiThread
import org.jetbrains.anko.toast

/**
 * Created by Brian Parma on 1/27/16.
 *
 * Sends a 'click' to the server.
 */
class ClickService : IntentService("ClickService"), AnkoLogger {
    override fun onHandleIntent(intent: Intent?) {
        info("sending click")

        var retry = 2
        while ( PirageApi.sendClick() == null ) {
            if ( --retry <= 0 )
                break
        }

        val failed = retry == 0
        onUiThread {
            if ( failed )
                toast("failed to send click request")
            else
                toast("sent request")
        }
    }
}