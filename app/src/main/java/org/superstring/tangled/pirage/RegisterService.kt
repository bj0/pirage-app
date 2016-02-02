package org.superstring.tangled.pirage

import android.app.IntentService
import android.content.Intent
import com.google.android.gms.gcm.GcmPubSub
import com.google.android.gms.gcm.GoogleCloudMessaging
import com.google.android.gms.iid.InstanceID

/**
 * Created by Brian Parma on 1/28/16.
 *
 * Registers for receiving pub/sub notifications from pirage server.
 */

class RegisterService : IntentService("RegisterService") {
    override fun onHandleIntent(intent: Intent?) {
        val instanceId = InstanceID.getInstance(this)
        val token = instanceId.getToken(this.getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null)

        // register for pub/sub topic
        GcmPubSub.getInstance(this).subscribe(token, "/topics/pirage", null)
    }
}