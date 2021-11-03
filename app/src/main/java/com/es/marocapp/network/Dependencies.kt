package com.es.marocapp.network

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.InputStream
import java.security.MessageDigest
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


/**
 * The Base class for setting up dependencies of network client
 */

abstract class Dependencies {

    protected fun <S> provideRestApi(context: Context, classService: Class<S>): S {

        var httpClient: OkHttpClient

        httpClient = provideHttpClientManual(provideHttpLoggingInterceptor())

        val builder = Retrofit.Builder()
                .baseUrl(setBaseUrl())
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
        return builder.build().create(classService)
    }

    /**
     * Method to Provide Loggin Interceptor
     * @return interceptor of type HttpLogginInterceptor
     */

    protected fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (setLogs()) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return interceptor
    }

    /**
     * This generate OkHTTP Client and return it
     */
    protected fun provideHttpClientManual(interceptor: HttpLoggingInterceptor): OkHttpClient {
        try {

            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")//SSL
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory)
            builder.hostnameVerifier(object : HostnameVerifier {
                override fun verify(hostname: String, session: SSLSession): Boolean {
                    try {
                        var certs = session.getPeerCertificates()
                       // return checkIfCertificateMatch(hostname, certs)
                        return true
                    } catch (e: Exception) {
                    }
                    return false

                }
            })

            builder.addInterceptor(interceptor)
            builder.addInterceptor { chain ->
                val request = chain.request()
                val builder = request.newBuilder()
                try {
                    val headers = setHeaders()
                    if (headers != null && headers.size > 0 &&
                            request != null && request.url() != null &&
                            request.url().toString().contains(setBaseUrl())) {
                        synchronized(this) {
                            for ((key, value) in headers) {
                                builder.addHeader(key, value)
                                Log.e(key, value)
                            }
                        }
                    }
                } catch (e: Exception) {
                    var a = 0
                }
                chain.proceed(builder.build())
            }

            val timeout = setTimeOut()
            builder.connectTimeout(timeout.toLong(), TimeUnit.SECONDS)
            builder.readTimeout(timeout.toLong(), TimeUnit.SECONDS)
            builder.writeTimeout(timeout.toLong(), TimeUnit.SECONDS)

            return builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    /**
     * This function match public key with local manualy and return true if mathced
     */
    fun checkIfCertificateMatch(hostname: String, serverCertsList: Array<Certificate>): Boolean {
        var isMatch = false

        try {
            // Check if SSL Pinning Enabled
            if (!enableSSL()) {
                isMatch = true
                return isMatch
            }


            if (serverCertsList != null) {
                for (i in 0 until serverCertsList.size) {
                    if (serverCertsList[0].publicKey != null) {
                        val md = MessageDigest.getInstance("SHA-256")
                        md.update(serverCertsList[0].publicKey.getEncoded())
                        val digest = md.digest();
                        val certformatch = String(android.util.Base64.encode(digest, android.util.Base64.DEFAULT));
                        val serverPublicKey = certformatch.trim()
                        val localPublicKeys = setSslCertificate()
                        if (localPublicKeys != null) {
                            for (j in 0 until localPublicKeys.size) {
                                if (localPublicKeys.get(j).equals(serverPublicKey, true)) {
                                    isMatch = true
                                    break
                                }
                            }
                        }
                        if (isMatch) {
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
        }

        return isMatch
    }

    /**
     *  Method to set logs
     */
    open fun setLogs() = true

    open fun enableSSL() = false

    open fun setSslCertificate(): ArrayList<String>? = null

    open fun setSslCertificateFile(): InputStream? = null

    open fun setDomain(): String = ""

//    open fun sslUnSafePinning() = false

    /**
     * Method to set Connection timeout
     * @return 30 of type Int
     */

    abstract fun setTimeOut(): Int

    abstract fun setBaseUrl(): String
    abstract fun setHeaders(): ConcurrentHashMap<String, String>
    abstract fun setContext(): Context

    //#########################

    /**
     * Method to Provide OK Http client, with api specfic timeout
     * @param interceptor of Type HttpLogginInterceptor
     * @return okBuilder of type OkHttpClient
     */
//    protected fun provideOkHttpClientDefault(context: Context, interceptor: HttpLoggingInterceptor): OkHttpClient {
//        val okBuilder = OkHttpClient.Builder()
//        okBuilder.addInterceptor(interceptor)
//        okBuilder.addInterceptor { chain ->
//            val request = chain.request()
//            val builder = request.newBuilder()
//            val headers = setHeaders()
//            try {
//                if (headers != null && headers.size > 0) {
//                    for ((key, value) in headers) {
//                        builder.addHeader(key, value)
//                        Log.e(key, value)
//                    }
//                }
//            } catch (e: Exception) {
//                var a = 0
//            }
//            chain.proceed(builder.build())
//        }
//
//        val timeout = setTimeOut()
//        okBuilder.connectTimeout(timeout.toLong(), TimeUnit.SECONDS)
//        okBuilder.readTimeout(timeout.toLong(), TimeUnit.SECONDS)
//        okBuilder.writeTimeout(timeout.toLong(), TimeUnit.SECONDS)
//
//
//        //################ SSL Pinning Implementation Start
//
////        val certificatePinner = CertificatePinner.Builder()
////                .add(setDomain(), "sha256/" + setSslCertificate())
////                .build()
////
////        val connectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
////        connectionSpec.tlsVersions(TlsVersion.TLS_1_2).build()
////
////        var tlsSocketFactory = TLSSocketFactory()
////        try {
////            okBuilder.sslSocketFactory(tlsSocketFactory, tlsSocketFactory.systemDefaultTrustManager())
////        } catch (e: KeyManagementException) {
////            Log.d(TAG, "Failed to create Socket connection ")
////            e.printStackTrace()
////        } catch (e: NoSuchAlgorithmException) {
////            Log.d(TAG, "Failed to create Socket connection ")
////            e.printStackTrace()
////        }
////
////
////        okBuilder.certificatePinner(certificatePinner)
////
////        okBuilder.connectionSpecs(Collections.singletonList(connectionSpec.build()))
////
////        return okBuilder.build()
//
//        //################ SSL Pinning Implementation End
//
//
//        // loading CAs from an InputStream
//// Load CAs from an InputStream
//
//        val certificateFactory = CertificateFactory.getInstance("X.509")
//
//        val inputStream = context.getResources().getAssets().open("certificates/signedcert.crt"); //(.crt)
//        val certificate = certificateFactory.generateCertificate(inputStream)
//        inputStream?.close()
//
//        // Create a KeyStore containing our trusted CAs
//        val keyStoreType = KeyStore.getDefaultType()
//        val keyStore = KeyStore.getInstance(keyStoreType)
//        keyStore.load(null, null)
//        keyStore.setCertificateEntry("ca", certificate)
//
//        // Create a TrustManager that trusts the CAs in our KeyStore.
//        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
//        val trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm)
//        trustManagerFactory.init(keyStore)
//
//        val trustManagers = trustManagerFactory.trustManagers
//        val x509TrustManager = trustManagers[0] as X509TrustManager
//
//
//        // Create an SSLSocketFactory that uses our TrustManager
//        val sslContext = SSLContext.getInstance("SSL")
//        sslContext.init(null, arrayOf<TrustManager>(x509TrustManager), null)
//        var sslSocketFactory = sslContext.socketFactory
//
//        //create Okhttp client
//        val client = OkHttpClient.Builder()
//                .sslSocketFactory(sslSocketFactory, x509TrustManager)
//                .build()
//        return client
//
//    }

//    protected fun provideHttpUnSafeClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
//        try {
//
//            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
//                @Throws(CertificateException::class)
//                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
//                }
//
//                @Throws(CertificateException::class)
//                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
//                }
//
//                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
//                    return arrayOf()
//                }
//            })
//
//            // Install the all-trusting trust manager
//            val sslContext = SSLContext.getInstance("SSL")
//            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
//            // Create an ssl socket factory with our all-trusting manager
//            val sslSocketFactory = sslContext.socketFactory
//
//            val builder = OkHttpClient.Builder()
//            builder.sslSocketFactory(sslSocketFactory)
//            builder.hostnameVerifier(object : HostnameVerifier {
//                override fun verify(hostname: String, session: SSLSession): Boolean {
//                    return true
//                }
//            })
//
//
//            builder.addInterceptor(interceptor)
//            builder.addInterceptor { chain ->
//                val request = chain.request()
//                val builder = request.newBuilder()
//                val headers = setHeaders()
//                try {
//                    if (headers != null && headers.size > 0) {
//                        for ((key, value) in headers) {
////                            builder.addHeader(key, value)
//                            builder.header(key, value)
//                            Log.e(key, value)
//                        }
//                    }
//                } catch (e: Exception) {
//                    var a = 0
//                }
//                chain.proceed(builder.build())
//            }
//
//            val timeout = setTimeOut()
//            builder.connectTimeout(timeout.toLong(), TimeUnit.SECONDS)
//            builder.readTimeout(timeout.toLong(), TimeUnit.SECONDS)
//            builder.writeTimeout(timeout.toLong(), TimeUnit.SECONDS)
//
//            return builder.build()
//        } catch (e: Exception) {
//            throw RuntimeException(e)
//        }
//
//    }


}