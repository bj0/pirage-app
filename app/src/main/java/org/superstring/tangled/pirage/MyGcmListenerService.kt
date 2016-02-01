package org.superstring.tangled.pirage

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import com.google.android.gms.gcm.GcmListenerService
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

/**
 * Created by Brian Parma on 1/27/16.
 */

class MyGcmListenerService: GcmListenerService(), AnkoLogger {
    override fun onMessageReceived(from: String?, data: Bundle?) {
        info("got gcm message: $data")
        val intent = Intent("msg-received")
        // get garage open flag
        intent.putExtra("msg", data?.getString("mag"))
        // tell rest of app we got a msg
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}