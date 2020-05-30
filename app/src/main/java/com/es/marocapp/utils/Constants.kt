package com.es.marocapp.utils

import android.app.Application
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import android.text.format.Formatter.formatIpAddress
import androidx.core.content.ContextCompat.getSystemService
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

    fun createUserToken() : String{
        var token = SimpleDateFormat("yyyyMMddHHmmssSS")
            .format(Date()) + Random()
            .nextInt(999998) + "()"
        return token
    }

    fun getIPAddress(application: Application){
        val wm =
            application.getSystemService(WIFI_SERVICE) as WifiManager?
        APPLICATION_IP_ADDRESS = formatIpAddress(wm!!.connectionInfo.ipAddress)
    }

    fun getNumberMsisdn(number : String) : String{
        return "$number/MSISDN"
    }
}