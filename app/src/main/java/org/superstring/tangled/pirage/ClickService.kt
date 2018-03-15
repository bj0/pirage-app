package org.superstring.tangled.pirage

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.toast
import org.superstring.tangled.pirage.api.sendClick
import timber.log.pirage.info

/**
 * Created by Brian Parma on 1/27/16.
 *
 * Sends a 'click' to the server.
 */
class ClickService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        info { "on handle work" }
        launch(UI) {
            info { "try click" }
            tryClick(this@ClickService)
        }
    }

    companion object {
        suspend fun tryClick(context: Context) {
            info("sending click")

            for (retry in 0 until 2) {
                val response = sendClick()
                if (response != null) {
                    context.toast("sent request")
                    return
                }
            }

            context.toast("failed to send click request")
        }
    }
}