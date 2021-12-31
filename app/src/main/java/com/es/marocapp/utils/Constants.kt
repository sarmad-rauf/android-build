package com.es.marocapp.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.database.Cursor
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.OpenableColumns
import android.text.format.Formatter.formatIpAddress
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.es.marocapp.R
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.model.responses.*
import com.github.florent37.tutoshowcase.TutoShowcase
import org.apache.http.conn.util.InetAddressUtils
import java.io.File
import java.net.InetAddress
import java.net.NetworkInterface
import java.security.SecureRandom
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


object Constants {


    lateinit var fatouratiTsavMatriculeDdValsMap: Map<String, String>
    var airtimeMaxNumOfRetries: String="0"
    var maxFileSizeUploadLimitInMBs: Int = 0
    lateinit var registrationProfiles: Array<String>
    var marocFatouratiLogoPath: String=""
    var selectedTSAVSpinnerPosition: Int=0
    var STEP2_3RESPONSE: BillPaymentFatoratiStepThreeResponse? = null
    lateinit var fatouratiTsavMatriculeDdVals: Array<String>
    var COMMISIONACCOUNTFRI: String = ""
    var COMMISIONACCOUNTBALANCE: String = "0"
    var WALLETACCOUNTBALANCE: String="0"
    var TAX_DETALS: String=":TaxDetails="
    lateinit var REASON_FOR_UPDATE_PROFILE: String
    lateinit var BILLTYPEINWI: String
    lateinit var HELPLINENUMBERAGENT: String

    //Iam bills list for which fatourati flow will be triggered
    lateinit var iamBillsTriggerFatouratiFlow: Array<String>

    //update profile  listner
    var shouldUpdate: Boolean=false

    lateinit var CityNameRegex: String
    val ACTIVE: String="ACTIVE"
    lateinit var fatouratiSeperateMenuBillNames: Array<String>

    //profile upgrade reasons
    lateinit var reasonUpgradeToLevelThree: String
    lateinit var reasonUpgradeToLevelTwo: String

    //profiles list  getting in preLoginData api to show Upgrade profile buton
    lateinit var upgradeSupportedProfiles: Array<String>

    //profile geting from forgot psw flow in getProfile api
      var UserProfileName: String=""

    //Error Msgs
    val SHOW_DEFAULT_ERROR = "SHOW_DEFAULT_ERROR"
    val SHOW_INTERNET_ERROR = "Please check your internet!"
    val SHOW_SERVER_ERROR = "Something went wrong!"

    //merchant agent profile name and array
    //var MERCHANT_AGENT_PROFILE_NAME = "MT Merchant Agent Account Profile"
     var MERCHANT_AGENT_PROFILE_NAME: String = ""
    var acountTypeList :ArrayList<String> = ArrayList()
     var CURRENT_ACOUNT_TYPE_SELECTED: String? = LanguageData.getStringValue("Wallet")
    lateinit var LAST_ACOUNT_TYPE_SELECTED:String
    lateinit var RECIEVER_ACOUNT_FRI:String

    lateinit var MERCHENTAGENTPROFILEARRAY:Array<String>


    //For Bill Payment Fee Charge on Bills Keys in Fatorati Bill use case
/*
    Summary:-
    forfait_facture --> Fee on per invoice
    forfait --> Fee on total amount of selected bills
    commission --> Fee on total amount of selected bills*/
    const val BILL_PAYMENT_TYPE_FORFAIT_FACTURE = "forfait_facture"  //Number of Bill * valeurFrais
    const val BILL_PAYMENT_TYPE_FORFAIT = "forfait" //valeurFrais + Total Amount
    const val BILL_PAYMENT_TYPE_COMISSION = "commission" //Amount * valeurFrais /100


    const val IDENTIFICATION_TYPE = "CNIC"
    const val SECRET_TYPE = "password"
    /*var TRANSFER_TYPE_PAYMENT = "INTEROP_TRANSFER"
    var MERCHANT_TYPE_PAYMENT = "CUSTOM_INTEROP_TRANSFER_SEND"
    var TYPE_PAYMENT = "PAYMENT"
    var TYPE_BILL_PAYMENT = "BILL_PAYMENT"
    var TYPE_COMMISSIONING = "COMMISSIONING"
    var OPERATION_TYPE_CREANCIER = "creancier"
    var OPERATION_TYPE_CREANCE = "creance"
    var OPERATION_TYPE_IMPAYES = "impayes"
    var TYPE_CASH_IN = "CASH_IN"
    var PAYMENT_TYPE_SEND_MONEY = "sendmoney"
    var PAYMENT_TYPE_INITIATE_MERCHANT = "initiatemerchant"*/

    var TRANSFER_TYPE_PAYMENT = ""
    var MERCHANT_TYPE_PAYMENT = ""
    var TYPE_PAYMENT = ""
    var TYPE_BILL_PAYMENT = ""
    var TYPE_COMMISSIONING = ""
    var OPERATION_TYPE_CREANCIER = ""
    var OPERATION_TYPE_CREANCE = ""
    var OPERATION_TYPE_IMPAYES = ""
    var TYPE_CASH_IN = ""
    var PAYMENT_TYPE_SEND_MONEY = ""
    var PAYMENT_TYPE_INITIATE_MERCHANT = ""

    //preLoginData
    var APP_DATE_FORMAT = "yyyy-mm-dd"
    var APP_CN_REGEX = "[a-zA-Z]{2}[0-9]{6}"
    var APP_CN_LENGTH = "8"
    var APP_OTP_REGEX = "^[a-zA-Z0-9]*$"
    var APP_OTP_LENGTH = 8
    var APP_MIN_PASSWORD_LENGTH = 8
    var APP_MAX_PASSWORD_LENGTH = 16
    var APP_DEFAULT_ACCOUNT_OTP_LENGTH by Delegates.notNull<Int>()
    var APP_DEFAULT_ACCOUNT_OTP_REGEX:String?= ""
    var APP_AIR_TIME_FIXE_REGEX:String?= ""
    var APP_ADDFAVORITE_NICK_LENGTH:Int?= 8
    var APP_MSISDN_PREFIX = "+000"
    var APP_MSISDN_LENGTH = "12"
    var APP_MSISDN_REGEX = ""
    var APP_MSISDN_POSTPAIDBILL_MOBILE_REGEX = ""
    var APP_MSISDN_POSTPAIDBILL_FIXE_REGEX = ""
    var APP_MSISDN_POSTPAIDBILL_INTERNET_REGEX = ""
    var APP_BILL_PAYMENT_CODE_REGEX = ""
    var APP_CIL_LENGTH = ""
    var APP_CIL_REGEX = ""
    var quickAmountsList: ArrayList<String> = arrayListOf()
    var quickRechargeAmountsList: ArrayList<String> = arrayListOf()
    var URL_FOR_FAQ = ""
    var URL_FOR_TERMSANDCONDITIONS = ""
    var APP_VERSION = ""
    var URL_FOR_UPDATE_APP = ""
    lateinit var KEY_FOR_WALLET_BALANCE_MAX:Array<String>
    var KEY_FOR_POST_PAID_TELECOM_BILL = ""
    var PREVIOUS_DAYS_TRANSACTION_COUNT = "30"
    var CASH_IN_VIA_CARD_URL = ""


    var APPLICATION_IP_ADDRESS = ""
    var CURRENT_DEVICE_ID = ""
    var CURRENT_NUMBER_DEVICE_ID = ""
    var CURRENT_CURRENCY_TYPE = ""
    var CURRENT_CURRENCY_TYPE_TO_SHOW = ""
    var AMOUNT_CONVERSION_VALUE = ""
    var HELPLINE_NUMBER = ""

    var HEADERS_AFTER_LOGINS = false
    var HEADERS_FOR_PAYEMNTS = false
    var CURRENT_USER_MSISDN = ""
    var CURRENT_USER_CREDENTIAL = ""
    var LOGGED_IN_USER = ""
    var LOGGED_IN_USER_COOKIE = ""
    var CURRENT_USER_NAME = ""
    var CURRENT_USER_EMAIL = ""
    var CURRENT_USER_FIRST_NAME = ""
    var CURRENT_USER_LAST_NAME = ""
    var CURRENT_USER_DATE_OF_BIRTH=""
    var CURRENT_USER_CITY=""
    var CURRENT_USER_ADRESS=""
    var CURRENT_USER_CIN=""
    var CURRENT_USER_MIDDLE_NAME=""


    //USER_PROFILE
    var IS_AGENT_USER = false
    var IS_CONSUMER_USER = false
    var IS_MERCHANT_USER = false

    var IS_DEFAULT_ACCOUNT_SET = false
    var IS_FIRST_TIME = true
    var isTutorialShowing = false
    var isFirstTimeTutorialShowing = 0

    const val OTP_LENGTH_PLACEHOLDER_TO_BE_REPLACED = "<otp-length>"

    lateinit var tutorialDashboardCashInViaCard: ImageView
    lateinit var tutorialCallIconHomeScreen: ImageView
    lateinit var tutorialQuickRechargeContainer: CardView
    var tutorialSendMoney: ConstraintLayout? = null
//    lateinit var tutorialSendMoney : View

    //Responses
    var balanceInfoAndResponse: BalanceInfoAndLimitResponse? = null
    var newbalanceInfoAndResponse: BalanceInfoAndLimitResponse? = null
    var getAccountsResponse: Account? = null
    lateinit var getAccountsResponseArray: ArrayList<Account>
    lateinit var loginWithCertResponse: LoginWithCertResponse
    lateinit var currentTransactionItem: History
    var mContactListArray: ArrayList<Contact> = arrayListOf()


    fun getCurrentDate(): String {
        val c = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = df.format(c)
        Log.i("CurrentTime", formattedDate)
        return formattedDate
    }

    fun getPreviousFromCurrentDate(
        currentDate: String,
        previousDaysTransactionCount: Int
    ): String {
        val dateFormat =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(currentDate)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, -previousDaysTransactionCount)
        val previousDateAsString = dateFormat.format(calendar.time)
        Log.i("CurrentTimePrevious", previousDateAsString)

        return previousDateAsString
    }

    fun createUserToken(): String {
        val token = SimpleDateFormat("yyyyMMddHHmmssSS", Locale.ENGLISH)
            .format(Date()) + SecureRandom()
            .nextInt(999998) + "($CURRENT_USER_MSISDN)"
        return token
    }

    fun getSelectedLanguage(): String {
        return LocaleManager.selectedLanguage
    }

    fun getIPAddress(application: Application) {
        val wm =
            application.getSystemService(WIFI_SERVICE) as WifiManager?
        APPLICATION_IP_ADDRESS = formatIpAddress(wm!!.connectionInfo.ipAddress)
    }

    fun getDeviceIPAddress(useIPv4: Boolean): String? {
        try {
            val networkInterfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (networkInterface in networkInterfaces) {
                val inetAddresses: List<InetAddress> =
                    Collections.list(networkInterface.getInetAddresses())
                for (inetAddress in inetAddresses) {
                    if (!inetAddress.isLoopbackAddress()) {
                        val sAddr: String = inetAddress.getHostAddress().uppercase()
                        val isIPv4: Boolean = InetAddressUtils.isIPv4Address(sAddr)
                        if (useIPv4) {
                            if (isIPv4) return sAddr
                        } else {
                            if (!isIPv4) {
                                // drop ip6 port suffix
                                val delim = sAddr.indexOf('%')
                                return if (delim < 0) sAddr else sAddr.substring(0, delim)
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

    fun setBase64EncodedString(str : String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LOGGED_IN_USER_COOKIE = Base64.getEncoder().encodeToString(str.toByteArray())
            Logger.debugLog("Base64", Base64.getEncoder().encodeToString(str.toByteArray()));
        } else {
            LOGGED_IN_USER_COOKIE =
                android.util.Base64.encodeToString(str.toByteArray(), android.util.Base64.NO_WRAP)
        }
        Logger.debugLog(
            "Base64",
            android.util.Base64.encodeToString(str.toByteArray(), android.util.Base64.NO_WRAP)
        );


    }

    fun getBase64EncryptedToString(encrptedString: String): String {
        val decodedBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getDecoder().decode(encrptedString)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val decodedString = String(decodedBytes)
        return decodedString
    }

    //Alias's from PreLoginData
    var NUMBER_MSISDN_ALIAS = ""
    var TRANSFER_RECEIVER_ALIAS = ""
    var MERCHANT_RECEIVER_ALIAS = ""
    var AIR_TIME_RECEIVER_ALIAS = ""
    var AIR_TIME_Pass_Store_RECEIVER_ALIAS = ""
    var AGNET_RECEIVER_ALIAS = ""
    var POST_PAID_MOBILE_ALIAS = ""
    var POST_PAID_FIXED_ALIAS = ""
    var POST_PAID_INTERNET_ALIAS = ""
    var FATOURATI_ALIAS = ""
    //HardCoded to add BillProviderContact in adding favourite
    var FAVOURITE_ALIAS = "@fatourati.sp/USER"

    fun getNumberMsisdn(number: String): String {
//        return "$number/MSISDN"
        return "$number$NUMBER_MSISDN_ALIAS"
    }

    fun getTransferReceiverAlias(number: String): String {
//        return "$number@hpss.sub.sp/SP"
        return "$number$TRANSFER_RECEIVER_ALIAS"
    }

    fun getMerchantReceiverAlias(number: String): String {
//        return "$number@hpss.mer.sp/SP"
        return "$number$MERCHANT_RECEIVER_ALIAS"
    }

    fun getAirTimeReceiverAlias(number: String): String {
//        return "$number@ocs.prepaid.sp/SP"
        return "$number$AIR_TIME_RECEIVER_ALIAS"
    }

    fun getAirTimePassStoreReceiverAlias(number: String): String {
//        return "$number@ocs.prepaid.sp/SP"
        return "$number$AIR_TIME_Pass_Store_RECEIVER_ALIAS"
    }

    fun getAgentReceiverAlias(number: String): String {
//        return "$number@hpss.mer.sp/SP"
        return "$number$AGNET_RECEIVER_ALIAS"
    }

    fun getPostPaidMobileDomainAlias(number: String): String {
//        return "$number@bscs.mobile.sp/SP"
        return "$number$POST_PAID_MOBILE_ALIAS"
    }

    fun getPostPaidFixedDomainAlias(number: String): String {
//        return "$number@bscs.fixed.sp/SP"
        return "$number$POST_PAID_FIXED_ALIAS"
    }

    fun getPostPaidInternetDomainAlias(number: String): String {
//        return "$number@bscs.internet.sp/SP"
        return "$number$POST_PAID_INTERNET_ALIAS"
    }

    fun getFatoratiAlias(number: String): String {
//        return "$number@fatourati.sp/SP"
        return "$number$FATOURATI_ALIAS"
    }

    fun getFavouriteAlias(number: String): String {
//        return "$number@fatourati.sp/SP"
        return "$number$FAVOURITE_ALIAS"
    }

    fun getFatoratiServiceProviderAlias(number: String, selectedCompanyServiceProvider:String): String {
//        return "$number@fatouratiCompany name/SP"
        var alias = FATOURATI_ALIAS.replace("fatourati.sp",selectedCompanyServiceProvider)
        return "$number$alias"
    }

    fun addAmountAndFee(amount: Double, fee: Double): String {
        return (amount + fee).toString()
    }

    fun addTwoValues(amount: Double, fee: Double): Double {
        return (amount + fee)
    }

    fun parseDateFromString(dateString: String): String {
        var myDate = ""
        var df: DateFormat = SimpleDateFormat("yyyyMMdd")
        val d: Date
        try {
            d = df.parse(dateString)
            df = SimpleDateFormat("dd/MM/yyyy")
            myDate = df.format(d)
        } catch (e: ParseException) {
        }

        return myDate
    }

    fun converValueToTwoDecimalPlace(value: Double): String {
        val result = String.format(Locale.ENGLISH, "%.2f", value)
        return result
    }


    fun getZoneFormattedDateAndTime(dateToFormat: String): String {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
        val output = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US)

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



    fun displayTutorial(
        activityContext: Activity,
        viewForShowignTutorial: View,
        tutorialDescrption: String,
        drawableIcon: Int = -1
    ) {
        isFirstTimeTutorialShowing++
        val tutShowCase = TutoShowcase.from(activityContext)
            .setContentView(R.layout.tutorial_custom_view)
            .setFitsSystemWindows(true)
            .on(viewForShowignTutorial)
            .addRoundRect()
            .onClick(View.OnClickListener { })
            .show()
        val x = viewForShowignTutorial.x
        val y = viewForShowignTutorial.y
        tutShowCase.setTextView(R.id.tv_tutorial_custom_view, tutorialDescrption)

        tutShowCase.setPosition(R.id.mainView, x, y)

        tutShowCase.setIcon(R.id.iv_tutorial_custom_view, drawableIcon)

        tutShowCase.setListener {
            when (isFirstTimeTutorialShowing) {
                //for dashboardCashInViaCardTutorial
                1 -> {
                    if (loginWithCertResponse.allowedMenu.CashInViaCard != null) {
                        displayTutorial(
                            activityContext, tutorialDashboardCashInViaCard,
                            LanguageData.getStringValue("CashInViaCardTutorial").toString(),
                            R.drawable.ic_tutorial_home_cash_in_wallet
                        )
                    } else {
                        isFirstTimeTutorialShowing = 2
                        displayTutorial(
                            activityContext,
                            tutorialCallIconHomeScreen,
                            LanguageData.getStringValue("CallTutorial").toString()
                        )
                    }
                }
                // for callIconHomeScreen Tutorial
                2 -> {
                    displayTutorial(
                        activityContext,
                        tutorialCallIconHomeScreen,
                        LanguageData.getStringValue("CallTutorial").toString()
                    )
                }
                // for Home Screen Quick Recharge Tutorial
                3 -> {
                    displayTutorial(
                        activityContext,
                        tutorialQuickRechargeContainer,
                        LanguageData.getStringValue("QuickRechargeTutorial").toString(),
                        R.drawable.ic_tutorial_home_quick_recharge
                    )
                }
                // for Send Money Tutorial
                4 -> {
                    tutorialSendMoney?.let {
                        displayTutorial(
                            activityContext,
                            it, LanguageData.getStringValue("SendMoneyTutorial").toString()
                        )
                    }
                    PrefUtils.addBoolean(
                        activityContext,
                        PrefUtils.PreKeywords.PREF_KEY_IS_SHOW_TUTORIALS,
                        false
                    )
                }
                5 -> {
                    isFirstTimeTutorialShowing = -1
                    isTutorialShowing = false
                    PrefUtils.addBoolean(
                        activityContext,
                        PrefUtils.PreKeywords.PREF_KEY_IS_SHOW_TUTORIALS,
                        false
                    )

                  //  Toast.makeText(activityContext, LanguageData.getStringValue("TutorialEnds"), Toast.LENGTH_LONG).show()
                }
                else -> {
                    isTutorialShowing = false
                    PrefUtils.addBoolean(
                        activityContext,
                        PrefUtils.PreKeywords.PREF_KEY_IS_SHOW_TUTORIALS,
                        false
                    )
                }
            }
        }
    }



    fun convertSpinnerArabicValue(spinnerVal: String): String {
        val arabicToEngVal = fatouratiTsavMatriculeDdValsMap[spinnerVal]
        return if(arabicToEngVal.isNullOrEmpty()) {
            spinnerVal
        }else arabicToEngVal
    }

    fun convertListToJson(validatedParams: ArrayList<ValidatedParam>): String {
        var jsonArrayStringFormat = "("
        for(index in validatedParams.indices)
        {
           jsonArrayStringFormat= jsonArrayStringFormat.plus(validatedParams[index].nomChamp.plus(":").plus(validatedParams[index].valChamp)).plus(",")
        }

        jsonArrayStringFormat= jsonArrayStringFormat.removeSuffix(",")
        jsonArrayStringFormat= jsonArrayStringFormat.plus(")")
        return jsonArrayStringFormat
    }
    fun convertStringToListOfValidatedParams(stringValidatedParams: String): ArrayList<ValidatedParam> {
        var listOfParams :ArrayList<ValidatedParam> = ArrayList()
        var stringWithoutBrackets =  stringValidatedParams.substringAfter("(")
        stringWithoutBrackets =stringWithoutBrackets.substringBefore(")")
        val result: List<String> =
            stringWithoutBrackets.split(",").map { it.trim() }
        for(index in result.indices){
            val params: List<String> =
                result[index].split(":").map { it.trim() }
            listOfParams.add(ValidatedParam(params[1],params[0]))

        }
        return listOfParams
    }

    fun isFileSizeVerified(uri: Uri, activity: Context?,isCamera: Boolean): Boolean {
        val file = File(uri.path)
        var sizeInMbs = 0
        sizeInMbs = if(isCamera) {
            file.length().toInt().div((1024*1024).toString().substringBefore('.').toInt())
        }else{
            getSize(activity!!,uri)?.toInt()
                ?.div((1024*1024).toString().substringBefore('.').toInt())!!
        }
        if (sizeInMbs != null) {
            return sizeInMbs<=maxFileSizeUploadLimitInMBs
        }else{
            return true
        }

    }

    fun getSize(context: Context, uri: Uri?): String? {
        var fileSize: String? = null
        val cursor: Cursor? = context.contentResolver
            .query(uri!!, null, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {

                // get file size
                val sizeIndex: Int = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (!cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getString(sizeIndex)
                }
            }
        } finally {
            cursor?.close()
        }
        return fileSize
    }



//    fun getItems(stringValidatedParams: String): List<ValidatedParam>? {
//        if (stringValidatedParams.isEmpty(json)) return Collections.emptyList()
//        val type: Type = object : TypeToken<List<ExpenseItem?>?>() {}.type
//        return Gson().fromJson<List<ExpenseItem>>(json, type)
//    }

    object EMVco {
        const val Payload_Format_Indicator_ID = "00"
        const val Payload_Format_Indicator_SIZE = "02"
        const val Payload_Format_Indicator_VALUE = "01"

        const val Point_Of_Initiation_Method_ID = "01"
        const val Point_Of_Initiation_Method_SIZE = "02"
        const val Point_Of_Initiation_Method_VALUE = "12"
        const val Point_Of_Initiation_Method_VALUE_STATIC = "11"

        const val Merchant_Account_Information_ID = "26"
        const val Merchant_Account_Information_SIZE = "91"
        const val Merchant_Account_Information_Value = ""

        const val Globally_Unique_Identifier_ID = "00"
        const val Globally_Unique_Identifier_SIZE = "32"
        const val Globally_Unique_Identifier_VALUE = "5bb66a92d69c0ea742dd4f754590fa0a"

        const val Encryption_Format_ID = "02"
        const val Encryption_Format_SIZE = "01"
        const val Encryption_Format_VALUE = "1"

        const val Paid_Entity_Reference_Format_ID = "05"
        const val Paid_Entity_Reference_Format_SIZE = "01"
        const val Paid_Entity_Reference_Format_VALUE = "0"

        const val Paid_Entity_Reference_ID = "06"
        const val Paid_Entity_Reference_SIZE = "24"
        // var Paid_Entity_Reference_VALUE=""

        const val Masked_Paid_Entity_Reference_ID = "07"
        const val Masked_Paid_Entity_Reference_SIZE_12 = "12"
        const val Masked_Paid_Entity_Reference_SIZE_13 = "13"
        //   var Masked_Paid_Entity_Reference_VALUE=""

        const val Currency_Transaction_ID = "53"
        const val Currency_Transaction_SIZE = "03"
        const val Currency_Transaction_VALUE = "504"

        const val Amount_Transaction_ID = "54"
        const val Amount_Transaction_SIZE = "05"
        const val Amount_Transaction_VALUE = "80000"

        const val Unreserved_Template_ID = "80"
        const val Unreserved_Template_SIZE = "62"
        const val Unreserved_Template_VALUE = ""

        const val Unreserved_Globally_Unique_Identifier_ID = "00"
        const val Unreserved_Globally_Unique_Identifier_SIZE = "32"
        const val Unreserved_Globally_Unique_Identifier_VALUE = "37b3a355b830b3bf0974d23608a6f162"

        const val Operation_Type_ID = "01"
        const val Operation_Type_SIZE = "01"
        const val Operation_Type_VALUE = "0"

        const val Signature_Format_ID = "04"
        const val Signature_Format_SIZE = "01"
        const val Signature_Format_VALUE = "0"

        const val QR_Version_ID = "05"
        const val QR_Version_SIZE = "06"
        const val QR_Version_VALUE = "010002"

        const val QR_Instance_ID = "06"
        const val QR_Instance_SIZE = "02"
        const val QR_Instance_VALUE = "01"

        const val static = "62220818QR transfer static"
        const val dynamic = "62230819QR dynamic transfer"

        const val CRC = "6304"
    }

    object MerchantEMVco {
        const val Payload_Format_Indicator_ID = "00"
        const val Payload_Format_Indicator_SIZE = "02"
        const val Payload_Format_Indicator_VALUE = "01"

        const val Point_Of_Initiation_Method_ID = "01"
        const val Point_Of_Initiation_Method_SIZE = "02"
        const val Point_Of_Initiation_Method_VALUE = "12"
        const val Point_Of_Initiation_Method_VALUE_STATIC = "11"

        const val Merchant_Account_Information_ID = "26"
        const val Merchant_Account_Information_SIZE = "91"
        const val Merchant_Account_Information_Value = ""

        const val Globally_Unique_Identifier_ID = "00"
        const val Globally_Unique_Identifier_SIZE = "32"
        const val Globally_Unique_Identifier_VALUE = "5bb66a92d69c0ea742dd4f754590fa0a"

        const val Encryption_Format_ID = "02"
        const val Encryption_Format_SIZE = "01"
        const val Encryption_Format_VALUE = "1"

        const val Paid_Entity_Reference_Format_ID = "05"
        const val Paid_Entity_Reference_Format_SIZE = "01"
        const val Paid_Entity_Reference_Format_VALUE = "0"

        const val Paid_Entity_Reference_ID = "06"
        const val Paid_Entity_Reference_SIZE = "24"
        // var Paid_Entity_Reference_VALUE=""

        const val Masked_Paid_Entity_Reference_ID = "07"
        const val Masked_Paid_Entity_Reference_SIZE_12 = "12"
        const val Masked_Paid_Entity_Reference_SIZE_13 = "13"
        //   var Masked_Paid_Entity_Reference_VALUE=""

        const val Merchant_Category_Code_ID = "52"
        const val Merchant_Category_Code_SIZE = "04"
        const val Merchant_Category_Code_VALUE = "5541"

        const val Currency_Transaction_ID = "53"
        const val Currency_Transaction_SIZE = "03"
        const val Currency_Transaction_VALUE = "504"

        const val Amount_Transaction_ID = "54"
        const val Amount_Transaction_SIZE = "05"
        const val Amount_Transaction_VALUE = "80000"

        const val Country_Code_ID = "58"
        const val Country_Code_SIZE = "02"
        const val Country_Code_VALUE = "MA"

        const val Merchant_Name_ID = "59"
        const val Merchant_Name_SIZE = "22"
        const val Merchant_Name_VALUE = "MENARA PEPINIERES SARL"

        const val Merchant_City_ID = "60"
        const val Merchant_City_SIZE = "04"
        const val Merchant_City_VALUE = "SAFI"

        const val Unreserved_Template_ID = "80"
        const val Unreserved_Template_SIZE = "62"
        const val Unreserved_Template_VALUE = ""

        const val Unreserved_Globally_Unique_Identifier_ID = "00"
        const val Unreserved_Globally_Unique_Identifier_SIZE = "32"
        const val Unreserved_Globally_Unique_Identifier_VALUE = "37b3a355b830b3bf0974d23608a6f162"

        const val Operation_Type_ID = "01"
        const val Operation_Type_SIZE = "01"
        const val Operation_Type_VALUE = "1"

        const val Signature_Format_ID = "04"
        const val Signature_Format_SIZE = "01"
        const val Signature_Format_VALUE = "0"

        const val QR_Version_ID = "05"
        const val QR_Version_SIZE = "06"
        const val QR_Version_VALUE = "010002"

        const val QR_Instance_ID = "06"
        const val QR_Instance_SIZE = "02"
        const val QR_Instance_VALUE = "01"

        const val static = "6304"
        const val dynamic = "6304"
    }
}