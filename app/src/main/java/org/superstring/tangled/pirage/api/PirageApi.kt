@file:Suppress("PackageDirectoryMismatch")

package org.superstring.tangled.pirage.api

import android.graphics.BitmapFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.superstring.tangled.pirage.MainApplication
import org.superstring.tangled.pirage.await
import java.io.IOException
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

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

data class PirageStatus(val dweetEnabled: Boolean, val locked: Boolean, val open: Boolean)

private const val server = "https://tangled.superstring.org"

/**
 * trustManagers is used to authorize the server's self-signed cert
 */
private val trustManagers by lazy {
    // assuming this can only be called after Application is created
    val cert = CertificateFactory.getInstance("X.509")
            .generateCertificate(MainApplication.context.assets.open("ca.crt")) as X509Certificate

    val trustStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
        load(null)
        setCertificateEntry(cert.subjectX500Principal.name, cert)
    }

    TrustManagerFactory.getInstance("X509").apply {
        init(trustStore)
    }.trustManagers
}

/**
 * keyManagers is used to load the client-authentication cert
 */
private val keyManagers by lazy {
    // assuming this can only be called after Application is created

    val keyStore = KeyStore.getInstance("PKCS12").apply {
        load(MainApplication.context.assets.open("client.p12"), "".toCharArray())
    }

    KeyManagerFactory.getInstance("X509").apply {
        init(keyStore, "".toCharArray())
    }.keyManagers
}

private val sslContext by lazy {
    SSLContext.getInstance("TLS").apply {
        init(keyManagers, trustManagers, null)
    }
}

suspend fun request(url: String): ByteArray? {
    return try {
        val request = Request.Builder()
                .url(url)
                .build()
        val client = OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustManagers[0] as X509TrustManager)
                .build()

        client.newCall(request).await().body().byteStream().readBytes()
    } catch (e: IOException) {
//        error("failed to send request: $e")
        null
    }
}

suspend fun post(url: String) = request(url)
suspend fun getString(url: String) = request(url)?.toString(Charset.defaultCharset())
suspend fun getBitmap(url: String) = request(url)?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }

suspend fun sendClick() = post("$server/click")

suspend fun getStatus() = getString("$server/status")?.let {
    val jso = JSONObject(it)
    PirageStatus(jso.getBoolean("dweet_enabled"), jso.getBoolean("locked"), jso.getBoolean("mag"))
} ?: PirageStatus(false, false, false)

suspend fun getImage() = getBitmap("$server/cam/image")

