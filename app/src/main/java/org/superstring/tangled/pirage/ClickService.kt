package org.superstring.tangled.pirage

import android.app.IntentService
import android.content.Intent
import org.jetbrains.anko.*

/**
 * Created by Brian Parma on 1/27/16.
 *
 * Sends a 'click' to the server.
 */
class ClickService : IntentService("ClickService"), AnkoLogger {
    override fun onHandleIntent(intent: Intent?) {
        info("sending click")

        PirageApi.sendClick()

        onUiThread {
            toast("sent request")
        }
    }
}