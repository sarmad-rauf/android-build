package com.es.marocapp.utils

import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat.getSystemService
import java.text.SimpleDateFormat
import java.util.*


object Constants {

    //Error Msgs
    val SHOW_DEFAULT_ERROR = "SHOW_DEFAULT_ERROR"
    val SHOW_INTERNET_ERROR = "SHOW_INTERNET_ERROR"
    val SHOW_SERVER_ERROR = "SHOW_SERVER_ERROR"

    const val Identificationtype = "CNIC"

    fun createUserToken() : String{
        var token = SimpleDateFormat("yyyyMMddHHmmssSS")
            .format(Date()) + Random()
            .nextInt(999998) + "()"
        return token
    }

    fun getIPAddress() : String{
//        val wm =
//            getSystemService(WIFI_SERVICE) as WifiManager?
//        val ip: String = Formatter.formatIpAddress(wm!!.connectionInfo.ipAddress)
        return "10.69.0.171"
    }

    fun getNumberMsisdn(number : String) : String{
        return "$number/MSISDN"
    }
}