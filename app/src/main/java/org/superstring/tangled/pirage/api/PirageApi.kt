package org.superstring.tangled.pirage.api

import android.graphics.BitmapFactory
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import org.superstring.tangled.pirage.await
import org.superstring.tangled.pirage.toast
import splitties.init.appCtx
import timber.log.pirage.debug
import timber.log.pirage.err
import java.io.IOException
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

/**
 * Created by Brian Parma on 1/27/16.
 *
 * Used to communicate with Pirage server.
 *
 * mucked together from:
 * http://nategood.com/client-side-certificate-authentication-in-ngi
 * https://gist.github.com/mtigas/952344
 *
 */

enum class RequestType { GET, POST }

/**
 * Status response from pirage server
 */
data class PirageStatus(val dweetEnabled: Boolean, val locked: Boolean, val open: Boolean)

/**
 * pirage server public address
 */
private const val SERVER = "https://tangled.superstring.org"

/**
 * trustManagers is used to authorize the server's self-signed cert
 */
private val trustManagers by lazy {
    // assuming this can only be called after Application is created
    val cert = CertificateFactory.getInstance("X.509")
            .generateCertificate(appCtx.assets.open("ca.crt")) as X509Certificate

    val trustStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
        load(null, null)
        setCertificateEntry(cert.subjectX500Principal.name, cert)
    }

    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
        init(trustStore)
    }.trustManagers
}

/**
 * keyManagers is used to load the client-authentication cert
 */
private val keyManagers by lazy {
    // assuming this can only be called after Application is created

    val keyStore = KeyStore.getInstance("PKCS12").apply {
        load(appCtx.assets.open("client.p12"), "".toCharArray())
    }

    KeyManagerFactory.getInstance("X509").apply {
        init(keyStore, null)
    }.keyManagers
}

/**
 * sslContext for opening TLS connection to pirage server
 */
private val sslContext by lazy {
    SSLContext.getInstance("TLS").apply {
        //        init(keyManagers, trustManagers, null)
        init(keyManagers, null, null)
    }
}

/**
 * pass an HTTPS request to pirage server
 */
suspend fun request(url: String, type: RequestType = RequestType.GET): ByteArray? {
    return try {
        val request = Request.Builder()
                .url(url)
                .apply { if (type == RequestType.POST) post(RequestBody.create(null, "")) }
                .build()
        val client = OkHttpClient.Builder()
//                .sslSocketFactory(sslContext.socketFactory, trustManagers[0] as X509TrustManager)
                .sslSocketFactory(sslContext.socketFactory)
                .build()
        val response = client.newCall(request).await()
        debug { "response code=${response.code()}" }
        when (response.code()) {
        // this can't be on the main thread
            200 -> withContext(CommonPool) { response.body().byteStream().readBytes() }
            else -> {
                toast("Response Code: ${response.code()}".also { err(it) })
                null
            }
        }
    } catch (e: Exception) {
        err(e) { "failed to send request" }
        null
    }
}

/**
 * send an empty post request to the url
 */
suspend fun post(url: String) = request(url, RequestType.POST)

/**
 * send a get request and return the response as a string
 */
suspend fun getString(url: String) = request(url)?.toString(Charset.defaultCharset())

/**
 * send a get request and return the response as a bitmap
 */
suspend fun getBitmap(url: String) = request(url)?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }

/**
 * send a click event to toggle pirage door
 */
suspend fun sendClick() = post("$SERVER/click")

/**
 * get status of pirage server
 */
suspend fun getStatus() = getString("$SERVER/status")?.let {
    try {
        val jso = JSONObject(it)
        PirageStatus(
                jso.getBoolean("notify_enabled"),
                jso.getBoolean("locked"),
                jso.getBoolean("mag"))
    } catch (e: Exception) {
        err(e) { "failed to parse json" }
        null
    }
} ?: PirageStatus(false, false, false)

/**
 * get image from pirage cam
 */
suspend fun getImage() = getBitmap("$SERVER/cam/image")

