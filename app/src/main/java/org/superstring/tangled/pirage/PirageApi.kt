package org.superstring.tangled.pirage

import android.graphics.BitmapFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
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
object PirageApi : AnkoLogger {
    val server = "https://tangled.superstring.org"

    /**
     * trustManagers is used to authorize the server's self-signed cert
     */
    val trustManagers by lazy {
        // assuming this can only be called after Application is created
        val ctx = MainApplication.context
        val certFactory = CertificateFactory.getInstance("X.509")
        val cert = certFactory.generateCertificate(ctx.assets.open("ca.crt")) as X509Certificate
        val alias = cert.subjectX500Principal.name

        val trustStore = KeyStore.getInstance(KeyStore.getDefaultType())
        trustStore.load(null)
        trustStore.setCertificateEntry(alias, cert)

        val tmf = TrustManagerFactory.getInstance("X509")
        tmf.init(trustStore)
        tmf.trustManagers
    }

    /**
     * keyManagers is used to load the client-authentication cert
     */
    val keyManagers by lazy {
        // assuming this can only be called after Application is created
        val ctx = MainApplication.context

        val keyStore = KeyStore.getInstance("PKCS12")
        val fis = ctx.assets.open("client.p12")
        keyStore.load(fis, "".toCharArray())

        val kmf = KeyManagerFactory.getInstance("X509")
        kmf.init(keyStore, "".toCharArray())
        kmf.keyManagers
    }

    val sslContext by lazy {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(keyManagers, trustManagers, null)
        sslContext
    }

    fun sendRequest(type: String, url: String): ByteArrayOutputStream? {
        val client = OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustManagers[0] as X509TrustManager)
                .build()

        try {
            val request = Request.Builder()
                    .url(url)
                    .build();

            val baos = ByteArrayOutputStream()
            client.newCall(request).execute().body().byteStream().copyTo(baos)
            return baos
        } catch (e: IOException) {
            error("failed to send request: $e")
            return null
        }

//        val url = URL(url)
//        val connection = url.openConnection() as HttpURLConnection
//        val baos = ByteArrayOutputStream()
//        try {
//            if (connection is HttpsURLConnection) connection.sslSocketFactory = sslContext.socketFactory
//            connection.requestMethod = type
//            connection.connectTimeout = 1500
//            connection.readTimeout = 1500
//            connection.inputStream.copyTo(baos)
//            return baos
//        } catch(e: IOException) {
//            error("failed to send request: ${e.toString()}")
//            return null
//        } finally {
//            connection.disconnect()
//        }
    }

    fun post(url: String) = sendRequest("POST", url)
    fun getString(url: String) = sendRequest("GET", url)?.toString()
    fun getBitmap(url: String) = sendRequest("GET", url)?.let { BitmapFactory.decodeByteArray(it.toByteArray(), 0, it.size()) }

    fun sendClick() = post(server + "/click")

    fun getStatus() = getString(server + "/status")?.let {
        val jso = JSONObject(it)
        PirageStatus(jso.getBoolean("dweet_enabled"), jso.getBoolean("locked"), jso.getBoolean("mag"))
    } ?: PirageStatus(false, false, false)

    fun getImage() = getBitmap(server + "/cam/image")
}

data class PirageStatus(val dweetEnabled: Boolean, val locked: Boolean, val open: Boolean)