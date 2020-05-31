package com.es.marocapp.utils

import android.app.Application
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.os.Build
import android.text.format.Formatter.formatIpAddress
import java.text.SimpleDateFormat
import java.util.*


object Constants {

    //Error Msgs
    val SHOW_DEFAULT_ERROR = "SHOW_DEFAULT_ERROR"
    val SHOW_INTERNET_ERROR = "SHOW_INTERNET_ERROR"
    val SHOW_SERVER_ERROR = "SHOW_SERVER_ERROR"

    const val IDENTIFICATION_TYPE = "CNIC"
    const val SECRET_TYPE = "password"

    var APP_DATE_FORMAT = "yyyy-mm-dd"
    var APP_CN_REGEX = "[a-zA-Z]{2}[0-9]{6}"
    var APP_CN_LENGTH = "8"
    var APP_MSISDN_PREFIX = "+000"
    var APP_MSISDN_LENGTH = "12"

    var APPLICATION_IP_ADDRESS = ""
    var CURRENT_DEVICE_ID = ""

    var HEADERS_AFTER_LOGINS = false
    var CURRENT_USER_MSISDN = ""
    var LOGGED_IN_USER = ""
    var LOGGED_IN_USER_COOKIE = ""

    fun createUserToken() : String{
        var token = SimpleDateFormat("yyyyMMddHHmmssSS")
            .format(Date()) + Random()
            .nextInt(999998) + "($LOGGED_IN_USER)"
        return token
    }

    fun createUserLoggedInToken() : String{
        var token = SimpleDateFormat("yyyyMMddHHmmssSS")
            .format(Date()) + Random()
            .nextInt(999998) + "($CURRENT_USER_MSISDN)"
        return token
    }

    fun getIPAddress(application: Application){
        val wm =
            application.getSystemService(WIFI_SERVICE) as WifiManager?
        APPLICATION_IP_ADDRESS = formatIpAddress(wm!!.connectionInfo.ipAddress)
    }

    fun setBase64EncodedString(str : String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LOGGED_IN_USER_COOKIE = Base64.getEncoder().encodeToString(str.toByteArray())
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    fun getBase64EncryptedToString(encrptedString : String) : String{
        val decodedBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getDecoder().decode(encrptedString)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val decodedString = String(decodedBytes)
        return decodedString
    }

    fun getNumberMsisdn(number : String) : String{
        CURRENT_USER_MSISDN = number
        return "$number/MSISDN"
    }
}