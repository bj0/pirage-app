package org.superstring.tangled.pirage

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

/**
 * Created by Brian Parma on 1/27/16.
 *
 * Used to communicate with Pirage server.
 */
object PirageApi: AnkoLogger {
    val server = "https://tangled.superstring.org"
    val sslContext by lazy {
        // assuming this can only be called after Application is created
        val ctx = MainApplication.context!!
        val pass = "taco"
        val certFactory = CertificateFactory.getInstance("X.509")
        val cert = certFactory.generateCertificate(ctx.assets.open("ca.crt")) as X509Certificate
        val alias = cert.subjectX500Principal.name
        val trustStore = KeyStore.getInstance(KeyStore.getDefaultType())
        trustStore.load(null)
        trustStore.setCertificateEntry(alias, cert)

        val keyStore = KeyStore.getInstance("PKCS12")
        val fis = ctx.assets.open("client.p12")
        keyStore.load(fis, "".toCharArray())

        val kmf = KeyManagerFactory.getInstance("X509")
        kmf.init(keyStore, pass.toCharArray())
        val keyManagers = kmf.keyManagers

        val tmf = TrustManagerFactory.getInstance("X509")
        tmf.init(trustStore)
        val trustManagers = tmf.trustManagers

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(keyManagers, trustManagers, null)
        sslContext
    }

    fun sendRequest(type: String, url: String): String? {
        val url = URL(url)
        val connection = url.openConnection() as HttpURLConnection
        try {
            if (connection is HttpsURLConnection) connection.sslSocketFactory = sslContext.socketFactory
            connection.requestMethod = type
            connection.connectTimeout = 1500
            connection.readTimeout = 1500
            return connection.inputStream.reader().use { it.readText() }
        } catch(e: IOException) {
            error("failed to send request: ${e.toString()}")
            return null
        } finally {
            connection.disconnect()
        }
    }

    fun sendClick() = sendRequest("POST", server + "/click")

    fun getStatus() = sendRequest("GET", server + "/status")?.let {
        val jso = JSONObject(it)
        PirageStatus(jso.getBoolean("dweet_enabled"), jso.getBoolean("locked"), jso.getBoolean("mag"))
    } ?: PirageStatus(false, false, false)

}

data class PirageStatus(val dweetEnabled: Boolean, val locked: Boolean, val open: Boolean)