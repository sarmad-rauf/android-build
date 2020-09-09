package com.es.marocapp.utils

import android.app.Application
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.os.Build
import android.text.format.Formatter.formatIpAddress
import android.util.Log
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.model.responses.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object Constants {

    //Error Msgs
    val SHOW_DEFAULT_ERROR = "SHOW_DEFAULT_ERROR"
    val SHOW_INTERNET_ERROR = "Please check your internet!"
    val SHOW_SERVER_ERROR = "Something went wrong!"

    const val IDENTIFICATION_TYPE = "CNIC"
    const val SECRET_TYPE = "password"
    const val TRANSFER_TYPE_PAYMENT = "INTEROP_TRANSFER"
    const val MERCHANT_TYPE_PAYMENT = "CUSTOM_INTEROP_TRANSFER_SEND"
    const val TYPE_PAYMENT = "PAYMENT"
    const val TYPE_BILL_PAYMENT = "BILL_PAYMENT"
    const val TYPE_COMMISSIONING = "COMMISSIONING"
    const val OPERATION_TYPE_CREANCIER = "creancier"
    const val OPERATION_TYPE_CREANCE = "creance"
    const val OPERATION_TYPE_IMPAYES = "impayes"
    const val TYPE_CASH_IN = "CASH_IN"
    const val PAYMENT_TYPE_SEND_MONEY = "sendmoney"
    const val PAYMENT_TYPE_INITIATE_MERCHANT = "initiatemerchant"

    //preLoginData
    var APP_DATE_FORMAT = "yyyy-mm-dd"
    var APP_CN_REGEX = "[a-zA-Z]{2}[0-9]{6}"
    var APP_CN_LENGTH = "8"
    var APP_OTP_REGEX = "^[a-zA-Z0-9]*$"
    var APP_OTP_LENGTH = 8
    var APP_DEFAULT_ACCOUNT_OTP_LENGTH= 6
    var APP_MSISDN_PREFIX = "+000"
    var APP_MSISDN_LENGTH = "12"
    var APP_MSISDN_REGEX = ""
    var APP_MSISDN_POSTPAIDBILL_MOBILE_REGEX=""
    var APP_MSISDN_POSTPAIDBILL_FIXE_REGEX=""
    var APP_MSISDN_POSTPAIDBILL_INTERNET_REGEX=""
    var APP_BILL_PAYMENT_CODE_REGEX=""
    var APP_CIL_LENGTH = ""
    var APP_CIL_REGEX = ""
    var quickAmountsList : ArrayList<String> = arrayListOf()
    var quickRechargeAmountsList : ArrayList<String> = arrayListOf()
    var URL_FOR_FAQ =""
    var URL_FOR_TERMSANDCONDITIONS =""
    var APP_VERSION =""
    var URL_FOR_UPDATE_APP =""
    var KEY_FOR_WALLET_BALANCE_MAX =""
    var PREVIOUS_DAYS_TRANSACTION_COUNT ="30"
    var CASH_IN_VIA_CARD_URL =""


    var APPLICATION_IP_ADDRESS = ""
    var CURRENT_DEVICE_ID = ""
    var CURRENT_NUMBER_DEVICE_ID = ""
    var CURRENT_CURRENCY_TYPE = ""
    var CURRENT_CURRENCY_TYPE_TO_SHOW = ""
    var AMOUNT_CONVERSION_VALUE= ""
    var HELPLINE_NUMBER= ""

    var HEADERS_AFTER_LOGINS = false
    var HEADERS_FOR_PAYEMNTS = false
    var CURRENT_USER_MSISDN = ""
    var CURRENT_USER_CREDENTIAL = ""
    var LOGGED_IN_USER = ""
    var LOGGED_IN_USER_COOKIE = ""
    var CURRENT_USER_NAME =""
    var CURRENT_USER_EMAIL=""
    var CURRENT_USER_FIRST_NAME=""
    var CURRENT_USER_LAST_NAME=""

    //USER_PROFILE
    var IS_AGENT_USER = false
    var IS_CONSUMER_USER = false
    var IS_MERCHANT_USER = false

    var IS_DEFAULT_ACCOUNT_SET = false
    var IS_FIRST_TIME = true
    
    //Responses
    lateinit var balanceInfoAndResponse : BalanceInfoAndLimitResponse
      var getAccountsResponse : Account? =null
    lateinit var getAccountsResponseArray : ArrayList<Account>
    lateinit var loginWithCertResponse : LoginWithCertResponse
    lateinit var currentTransactionItem : History
    var mContactListArray : ArrayList<Contact> = arrayListOf()

    fun getCurrentDate() : String{
        /*val calendar = Calendar.getInstance(TimeZone.getDefault())

        val currentYear = calendar[Calendar.YEAR].toString()
        val currentMonth = (calendar[Calendar.MONTH] + 1).toString()
        val currentDay = calendar[Calendar.DAY_OF_MONTH].toString()
        var formattedDate = "$currentYear-$currentMonth-$currentDay"
        return formattedDate*/
        val c = Calendar.getInstance().time
        Log.i("CurrentTime",c.toString())

//        val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = df.format(c)
        Log.i("CurrentTime",formattedDate)
        return formattedDate
    }

    fun getPreviousFromCurrentDate (
        currentDate: String,
        previousDaysTransactionCount: Int
    ) : String{
        val dateFormat =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(currentDate)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, -previousDaysTransactionCount)
        val previousDateAsString = dateFormat.format(calendar.time)
        Log.i("CurrentTimePrevious",previousDateAsString)

        return previousDateAsString
    }

    fun createUserToken() : String{
        var token = SimpleDateFormat("yyyyMMddHHmmssSS",Locale.ENGLISH)
            .format(Date()) + Random()
            .nextInt(999998) + "($LOGGED_IN_USER)"
        return token
    }

    fun getSelectedLanguage() : String{
        return LocaleManager.selectedLanguage
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
            Log.d("Base64",Base64.getEncoder().encodeToString(str.toByteArray()));
        } else {
            LOGGED_IN_USER_COOKIE = android.util.Base64.encodeToString(str.toByteArray(), android.util.Base64.NO_WRAP)
        }
        Log.d("Base64",android.util.Base64.encodeToString(str.toByteArray(), android.util.Base64.NO_WRAP));


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
        return "$number/MSISDN"
    }

    fun getTransferReceiverAlias(number: String) : String{
        return "$number@hpss.sub.sp/SP"
    }

    fun getMerchantReceiverAlias(number: String) : String{
        return "$number@hpss.mer.sp/SP"
    }

    fun getAirTimeReceiverAlias(number : String) : String{
        return "$number@ocs.prepaid.sp/SP"
    }

    fun getAgentReceiverAlias(number: String) : String{
        return "$number@hpss.mer.sp/SP"
    }

    fun getPostPaidMobileDomainAlias(number: String) : String{
        return "$number@bscs.mobile.sp/SP"
    }

    fun getPostPaidFixedDomainAlias(number: String) : String{
        return "$number@bscs.fixed.sp/SP"
    }

    fun getPostPaidInternetDomainAlias(number: String) : String{
        return "$number@bscs.internet.sp/SP"
    }

    fun getFatoratiAlias(number: String) :String{
        return "$number@fatourati.sp/SP"
    }

    fun addAmountAndFee(amount : Double, fee : Double): String{
        return (amount+fee).toString()
    }

    fun parseDateFromString(dateString : String) : String{
        var myDate = ""
        var df: DateFormat = SimpleDateFormat("yyyyMMdd")
        val d: Date
        try {
            d = df.parse(dateString)
            df = SimpleDateFormat("dd/MM/yyyy")
            myDate= df.format(d)
        } catch (e: ParseException) {
        }

        return myDate
    }

    fun getMonthFromParsedDate(date : String) : String{
        val d = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date)
        val cal = Calendar.getInstance()
        cal.time = d
        return SimpleDateFormat("MMMM").format(cal.time)
    }

    fun converValueToTwoDecimalPlace(value : Double) : String{
        val result = String.format(Locale.ENGLISH,"%.2f", value)
        return result
    }

    fun getZoneFormattedDateAndTime(dateToFormat : String): String {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val output = SimpleDateFormat("yyyy-MM-dd hh:mm a")

        var d: Date? = null
        var formatted: String = ""
        try {
            d = input.parse(dateToFormat)

         formatted = output.format(d)

        } catch (e: ParseException) {
            e.printStackTrace()
        }
        Log.i("DATE", "" + formatted)
        return formatted
    }
}