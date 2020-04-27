package com.es.marocapp.network.ssl

import android.annotation.SuppressLint
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.lang.System.load
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


/**
 * Utils package for setting up ssl certificates
 */


/**
 * @param certificatePath of Type String
 * @return context of type SSLContext
 */

inline fun getSllSocketContext(certificatePath: String): SSLContext {

    val cf: CertificateFactory = CertificateFactory.getInstance("X.509")

    val caInput: InputStream = BufferedInputStream(FileInputStream(certificatePath))
    val ca: X509Certificate = caInput.use {
        cf.generateCertificate(it) as X509Certificate
    }
    System.out.println("ca=" + ca.subjectDN)

    // Create a KeyStore containing our trusted CAs
    val keyStoreType = KeyStore.getDefaultType()
    val keyStore = KeyStore.getInstance(keyStoreType).apply {
        load(null, null)
        setCertificateEntry("ca", ca)
    }

    // Create a TrustManager that trusts the CAs inputStream our KeyStore
    val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
    val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
        init(keyStore)
    }

    // Create an SSLContext that uses our TrustManager
    val context: SSLContext = SSLContext.getInstance("TLS").apply {
        init(null, arrayOf<TrustManager>(OpenTrustManager()), null)
    }

//    sslContext.init(null, arrayOf<TrustManager>(OpenTrustManager()), null)

    return context
}


/**
 * @return trustManagers[0] of type X509TrustManager
 */

public inline fun getSystemDefaultTrustManager(): X509TrustManager {
    try {
        val trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(null as KeyStore);
        val trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.size != 1 || !(trustManagers[0] is X509TrustManager)) {
            throw  IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        return trustManagers[0] as X509TrustManager;
    } catch (e: GeneralSecurityException) {
        throw AssertionError(); // The system has no TLS. Just give up.
    }
}


@SuppressLint("TrustAllX509TrustManager")
class OpenTrustManager : X509TrustManager {


    @Throws(CertificateException::class)
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {

    }

    @Throws(CertificateException::class)
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {

    }

    override fun getAcceptedIssuers(): Array<X509Certificate?> {
        return arrayOfNulls(0)
    }
}
