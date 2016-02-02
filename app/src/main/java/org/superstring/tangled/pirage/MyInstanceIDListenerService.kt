package org.superstring.tangled.pirage

import com.google.android.gms.gcm.GoogleCloudMessaging
import com.google.android.gms.iid.InstanceID
import com.google.android.gms.iid.InstanceIDListenerService
import org.jetbrains.anko.startService

/**
 * Created by Brian Parma on 1/28/16.
 *
 * Called by GCM service when we need to re-register.  Boilerplate
 */

class MyInstanceIDListenerService : InstanceIDListenerService() {
    override fun onTokenRefresh() {
        // re-register to get new token
        startService<RegisterService>()
    }
}