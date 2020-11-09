package com.es.marocapp.network

import android.content.Context
import com.es.marocapp.BuildConfig
import com.es.marocapp.security.EncryptionUtils
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.RootValues
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList

public class ApiClient() : Dependencies() {


    lateinit var tecContext: Context
    private lateinit var serverAPI: NetworkApi

    val headerParams = ConcurrentHashMap<String, String>()

    private val apiTimeOutTime: Int = 30 // Seconds

    /**
     * static variable
     * Timeout and stuff
     */

    companion object {
        val newApiClientInstance = ApiClient()
    }

    /*
     * Primary Consturctor initialziation block
     */
    fun setInit(context: Context) {
        serverAPI = provideRestApi(context, NetworkApi::class.java)
        this.tecContext = context
    }


    /**
     *    Returns server api instance
     */

    fun getServerAPI(): NetworkApi {
        return serverAPI
    }


    /**
     * Returns server api instance with api specfic timeout
     */

//    fun getServerAPITemp(timeout: Int): NetworkApi {
//        serverAPI = provideRestApi(tecContext, NetworkApi::class.java, timeout)
//        return serverAPI
//    }

    /*
    Base URL Initialization
    You can take base url from your gradle as well
    */

    override fun setBaseUrl(): String {
        return BuildConfig.SERVER_URL
    }


    /**
     *   Header Initialization
     */
    @Synchronized
    override fun setHeaders(): ConcurrentHashMap<String, String> {

        headerParams.clear()


        headerParams["Accept"] = "application/json"
        headerParams["X-Forwarded-For"] = Constants.APPLICATION_IP_ADDRESS
        headerParams["token"] = Constants.createUserToken()
        headerParams["lang"] = Constants.getSelectedLanguage()
        if(Constants.HEADERS_AFTER_LOGINS){
            if(Constants.HEADERS_FOR_PAYEMNTS){
                headerParams["authorization"] = EncryptionUtils.encryptString("Basic "+Constants.CURRENT_USER_MSISDN+":"+Constants.CURRENT_USER_CREDENTIAL)
            }else{
                headerParams["authorization"] = Constants.LOGGED_IN_USER_COOKIE
            }
        }

        return headerParams
    }


    override fun setTimeOut(): Int = 30


    override fun setContext(): Context {
        return tecContext
    }

    override fun setLogs(): Boolean {
        return BuildConfig.LOG_ENABLED
    }

    override fun enableSSL(): Boolean {
        return BuildConfig.SSL_ENABLED
    }

   /* override fun setDomain(): String {
        return BuildConfig.SERVER_DOMAIN
    }*/

    override fun setSslCertificate(): ArrayList<String> {
       return RootValues.getInstance().keysPublicServerFromNdk
    }

    override fun setSslCertificateFile(): InputStream? {
        return null
    }


}


/////////Method to generate sha from public key cert
/*

Creating certificate from remote URL and writing to file (mycertfile.pem)
openssl s_client -showcerts -connect https://10.100.170.122/mobileapi/ </dev/null 2>/dev/null|openssl x509 -outform PEM >mycertfile.pem


Generating SHA256 hash of public key from a certificate (mycertfile.pem)
openssl x509 -in mycertfile.pem -pubkey -noout | openssl rsa -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64

openssl x509 -in c1b712d88c028033.crt -pubkey -noout | openssl rsa -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64

*/