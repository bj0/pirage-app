package org.superstring.tangled.pirage

import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by bjp on 9/20/17.
 */
/**
 * Suspend extension that allows suspend [Call] inside coroutine.
 *
 * @return Result of request or throw exception
 */
suspend fun Call.await(): Response = suspendCancellableCoroutine { continuation ->
    enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            continuation.resume(response)
        }

        override fun onFailure(call: Call, e: IOException) {
            // Don't bother with resuming the continuation if it is already cancelled.
            if (continuation.isCancelled) return
            continuation.resumeWithException(e)
        }
    })

    continuation.invokeOnCompletion {
        if (continuation.isCancelled)
            try {
                cancel()
            } catch (ex: Throwable) {
                //Ignore cancel exception
            }
    }
}