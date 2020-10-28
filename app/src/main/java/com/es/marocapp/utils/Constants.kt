package com.es.marocapp.utils

import android.app.Activity
import android.app.Application
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.os.Build
import android.text.format.Formatter.formatIpAddress
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.es.marocapp.R
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.locale.LocaleManager
import com.es.marocapp.model.responses.*
import com.github.florent37.tutoshowcase.TutoShowcase
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
    var APP_DEFAULT_ACCOUNT_OTP_LENGTH:Int?= 7
    var APP_DEFAULT_ACCOUNT_OTP_REGEX:String?= ""
    var APP_ADDFAVORITE_NICK_LENGTH:Int?= 8
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
    var isTutorialShowing = true
    var isFirstTimeTutorialShowing = 0

    lateinit var tutorialDashboardCashInViaCard : ImageView
    lateinit var tutorialCallIconHomeScreen : ImageView
    lateinit var tutorialQuickRechargeContainer : CardView
    lateinit var tutorialSendMoney : ConstraintLayout
//    lateinit var tutorialSendMoney : View

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

    fun showTutorial(activityContext : Activity, viewForShowignTutorial : View,tutorialDescrption : String ,drawableIcon : Int = -1){
        /*

            .focusOn(viewForShowignTutorial)
            .focusShape(FocusShape.ROUNDED_RECTANGLE)
            .roundRectRadius(10)
            .disableFocusAnimation()
            .
            .enableTouchOnFocusedView(true)
            */

        //Get screen size
        /*val location = IntArray(2)

        viewForShowignTutorial.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewForShowignTutorial.viewTreeObserver.removeOnGlobalLayoutListener(this)
                viewHegiht = viewForShowignTutorial.height //height is ready
                viewWidth = viewForShowignTutorial.width //width is ready
            }
        })*/

       /* val location = IntArray(2)
        viewForShowignTutorial.getLocationOnScreen(location)
        val viewPosX = location[0] + 330
        val viewPosY = location[1] + 160

        var viewWidth = -1
        var viewHegiht = -1


        viewHegiht = viewForShowignTutorial.height
        viewWidth = viewForShowignTutorial.width

        Log.d("viewPositionX",viewPosX.toString())
        Log.d("viewPositionY",viewPosY.toString())
        Log.d("viewPositionWidth",viewWidth.toString())
        Log.d("viewPositionHeight",viewHegiht.toString())

        val rootLayout: View = viewForShowignTutorial.rootView.findViewById(android.R.id.content)

        val viewLocation = IntArray(2)
        viewForShowignTutorial.getLocationInWindow(viewLocation)

        val rootLocation = IntArray(2)
        rootLayout.getLocationInWindow(rootLocation)

        val relativeLeft = viewLocation[0] - rootLocation[0]
        val relativeTop = viewLocation[1] - rootLocation[1]


        mFancyShowCaseView = FancyShowCaseView.Builder(activityContext)
            .focusRectAtPosition(relativeLeft,relativeTop,viewWidth,viewHegiht)
            .roundRectRadius(60)
            .customView(R.layout.tutorial_custom_view, object :
                OnViewInflateListener {
                override fun onViewInflated(view: View) {
                    val image = (view as RelativeLayout).findViewById<ImageView>(R.id.iv_tutorial_custom_view)
                    val tutorialText = (view as RelativeLayout).findViewById<TextView>(R.id.tv_tutorial_custom_view)

                    tutorialText.text = tutorialDescrption
                    val params = image.layoutParams as RelativeLayout.LayoutParams

                    if(drawableIcon==-1){
                        image.visibility = View.GONE

                        tutorialText.post {
                            params.leftMargin = mFancyShowCaseView!!.focusCenterX - image.width / 2
                            params.topMargin = mFancyShowCaseView!!.focusCenterY - mFancyShowCaseView!!.focusHeight - image.height
                            image.layoutParams = params
                        }
                    }else{
                        image.visibility = View.VISIBLE

                        image.setImageResource(drawableIcon)

                        image.post {
                            params.leftMargin = mFancyShowCaseView!!.focusCenterX - image.width / 2
                            params.topMargin = mFancyShowCaseView!!.focusCenterY - mFancyShowCaseView!!.focusHeight - image.height
                            image.layoutParams = params
                        }
                    }

                }
            })
            .closeOnTouch(true)
            .build()
*/
       /* mFancyShowCaseView = FancyShowCaseView.Builder(activityContext)
            .focusOn(viewForShowignTutorial)
            .focusShape(FocusShape.ROUNDED_RECTANGLE)
            .roundRectRadius(10)
            .enableTouchOnFocusedView(true)
            .customView(R.layout.tutorial_custom_view, object :
                OnViewInflateListener {
                override fun onViewInflated(view: View) {
                    val image = (view as RelativeLayout).findViewById<ImageView>(R.id.iv_tutorial_custom_view)
                    val tutorialText = (view as RelativeLayout).findViewById<TextView>(R.id.tv_tutorial_custom_view)

                    tutorialText.text = tutorialDescrption
                    val params = image.layoutParams as RelativeLayout.LayoutParams

                    if(drawableIcon==-1){
                        image.visibility = View.GONE

                        tutorialText.post {
                            params.leftMargin = mFancyShowCaseView!!.focusCenterX - image.width / 2
                            params.topMargin = mFancyShowCaseView!!.focusCenterY - mFancyShowCaseView!!.focusHeight - image.height
                            image.layoutParams = params
                        }
                    }else{
                        image.visibility = View.VISIBLE

                        image.setImageResource(drawableIcon)

                        image.post {
                            params.leftMargin = mFancyShowCaseView!!.focusCenterX - image.width / 2
                            params.topMargin = mFancyShowCaseView!!.focusCenterY - mFancyShowCaseView!!.focusHeight - image.height
                            image.layoutParams = params
                        }
                    }

                }
            })
            .closeOnTouch(true)
            .build()*/
//        mFancyShowCaseView?.show()
    }

    fun displayTutorial(activityContext : Activity, viewForShowignTutorial : View,tutorialDescrption : String ,drawableIcon : Int = -1) {
        isFirstTimeTutorialShowing++
        var tutShowCase = TutoShowcase.from(activityContext)
            .setContentView(R.layout.tutorial_custom_view)
            .setFitsSystemWindows(true)
            .on(viewForShowignTutorial)
            .addRoundRect()
            .onClick(View.OnClickListener { })
            .show()
        var x= viewForShowignTutorial.x
        var y=viewForShowignTutorial.y
        tutShowCase.setTextView(R.id.tv_tutorial_custom_view,tutorialDescrption)

        tutShowCase.setPosition(R.id.mainView,x,y)

        tutShowCase.setIcon(R.id.iv_tutorial_custom_view,drawableIcon)

        tutShowCase.setListener {
            when(isFirstTimeTutorialShowing){
                //for dashboardCashInViaCardTutorial
                1->{
                    if (loginWithCertResponse.allowedMenu.CashInViaCard != null) {
                        displayTutorial(activityContext, tutorialDashboardCashInViaCard,
                            LanguageData.getStringValue("CashInViaCardTutorial").toString(),
                            R.drawable.ic_tutorial_home_cash_in_wallet)
                    }else{
                        isFirstTimeTutorialShowing = 2
                        displayTutorial(activityContext,
                            tutorialCallIconHomeScreen,LanguageData.getStringValue("CallTutorial").toString())
                    }
                }
                // for callIconHomeScreen Tutorial
                2->{
                    displayTutorial(activityContext,
                        tutorialCallIconHomeScreen,LanguageData.getStringValue("CallTutorial").toString())
                }
                // for Home Screen Quick Recharge Tutorial
                3->{
                    displayTutorial(activityContext,
                        tutorialQuickRechargeContainer,LanguageData.getStringValue("QuickRechargeTutorial").toString()
                        ,R.drawable.ic_tutorial_home_quick_recharge)
                }
                // for Send Money Tutorial
                4->{
                    displayTutorial(activityContext,
                        tutorialSendMoney,LanguageData.getStringValue("SendMoneyTutorial").toString())
                }
                5->{
                    isFirstTimeTutorialShowing = -1
                    isTutorialShowing = false
                    Toast.makeText(activityContext,"Tutorials Ended",Toast.LENGTH_LONG).show()
                }
                else ->{
                    isTutorialShowing = false
                }
            }
        }
    }

        object EMVco{
        const val Payload_Format_Indicator_ID="00"
        const val Payload_Format_Indicator_SIZE="02"
        const val Payload_Format_Indicator_VALUE="01"

        const val Point_Of_Initiation_Method_ID="01"
        const val Point_Of_Initiation_Method_SIZE="02"
        const val Point_Of_Initiation_Method_VALUE="12"

        const val Merchant_Account_Information_ID="26"
        const val Merchant_Account_Information_SIZE="91"
        const val Merchant_Account_Information_Value=""

        const val Globally_Unique_Identifier_ID="00"
        const val Globally_Unique_Identifier_SIZE="32"
        const val Globally_Unique_Identifier_VALUE="5bb66a92d69c0ea742dd4f754590fa0a"

        const val Encryption_Format_ID="02"
        const val Encryption_Format_SIZE="01"
        const val Encryption_Format_VALUE="1"

        const val Paid_Entity_Reference_Format_ID="05"
        const val Paid_Entity_Reference_Format_SIZE="01"
        const val Paid_Entity_Reference_Format_VALUE="0"

        const val Paid_Entity_Reference_ID="06"
        const val Paid_Entity_Reference_SIZE="24"
       // var Paid_Entity_Reference_VALUE=""

        const val Masked_Paid_Entity_Reference_ID="07"
        const val Masked_Paid_Entity_Reference_SIZE_12="12"
        const val Masked_Paid_Entity_Reference_SIZE_13="13"
     //   var Masked_Paid_Entity_Reference_VALUE=""

        const val Currency_Transaction_ID="53"
        const val Currency_Transaction_SIZE="03"
        const val Currency_Transaction_VALUE="504"

            const val Amount_Transaction_ID="54"
            const val Amount_Transaction_SIZE="05"
            const val Amount_Transaction_VALUE="80000"

            const val dynamic="62230819QR dynamic transfer 6304FCA8"


    }
}