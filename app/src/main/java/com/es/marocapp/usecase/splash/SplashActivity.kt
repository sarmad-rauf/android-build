package com.es.marocapp.usecase.splash


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.BuildConfig
import com.es.marocapp.R
import com.es.marocapp.databinding.AcitivtySplashBinding
import com.es.marocapp.model.responses.GetPreLoginDataResponse
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import java.lang.reflect.Method


class SplashActivity : BaseActivity<AcitivtySplashBinding>() {

    private val READ_PHONE_STATE_REQUEST_CODE = 112

    private val PERMISSION_TAG = "permissions"

    override fun setLayout(): Int {
        return R.layout.acitivty_splash
    }

    lateinit var mActivityViewModel: SplashActivityViewModel


    override fun init(savedInstanceState: Bundle?) {

        mActivityViewModel = ViewModelProvider(this).get(SplashActivityViewModel::class.java)

        mDataBinding.apply {

            viewmodel = mActivityViewModel

        }

        setupPermissions()
        Constants.getIPAddress(application)
        subscribe()

        subscribeForTranslationsApiResponse()

    }

    fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_PHONE_STATE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(PERMISSION_TAG, "Permission to read phone state denied")
            makeRequestPermission()
        }else{
//            val telephonyManager =
//                application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
//            Constants.CURRENT_DEVICE_ID = telephonyManager!!.deviceId

            setDeviceIMEI()

            mActivityViewModel.requestForGetPreLoginDataApi(this@SplashActivity,BuildConfig.VERSION_NAME)
        }
    }

    private fun makeRequestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_PHONE_STATE),
            READ_PHONE_STATE_REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            READ_PHONE_STATE_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(PERMISSION_TAG, "Permission has been denied by user")
                } else {
//                    val telephonyManager =
//                        application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
                    setDeviceIMEI()
                }
            }
        }

        mActivityViewModel.requestForGetPreLoginDataApi(
            this@SplashActivity,
            BuildConfig.VERSION_NAME
        )
    }

    private fun subscribe() {
        val resultObserver = Observer<Boolean> {
            startNewActivityAndClear(this@SplashActivity, LoginActivity::class.java)
        }

        val preLoginDataObserver = Observer<GetPreLoginDataResponse> {
            if (it.responseCode.equals(ApiConstant.API_SUCCESS, true)) {
                Constants.APP_MSISDN_PREFIX = it.msisdnPrefix
                Constants.APP_MSISDN_LENGTH = it.msisdnLength
                Constants.APP_CN_LENGTH = it.cnLength
                Constants.APP_CN_REGEX = it.cnRegex
                Constants.APP_DATE_FORMAT = it.dateFormat
                Constants.CURRENT_CURRENCY_TYPE = it.currencyOnEwp
                Constants.CURRENT_CURRENCY_TYPE_TO_SHOW = it.currencyToShow
                if(it.quickAmounts.isNotEmpty()){
                    Constants.quickAmountsList.addAll(it.quickAmounts)
                }else{
                    Constants.quickAmountsList.apply {
                        add("50")
                        add("100")
                        add("250")
                        add("500")
                    }
                }
                mActivityViewModel.requestForTranslationsApi(this)
            } else {
                DialogUtils.showErrorDialoge(this@SplashActivity,it.description)
            }
        }

        val errorText = Observer<String> {
            DialogUtils.showErrorDialoge(this@SplashActivity,it)
        }

        mActivityViewModel.mHandler.observe(this, resultObserver)
        mActivityViewModel.preLoginDataResponseListener.observe(this, preLoginDataObserver)
        mActivityViewModel.errorText.observe(this, errorText)
    }

    private fun subscribeForTranslationsApiResponse() {
        mActivityViewModel.translationApiResponseListener.observe(this, Observer {
            if(it.responseCode.equals(ApiConstant.API_SUCCESS)){
                startNewActivityAndClear(this@SplashActivity, LoginActivity::class.java)
            /*    mApprovalsList.apply {
                    addAll(it.approvaldetails as ArrayList<Approvaldetail>)
                    mApprovalsItemAdapter.notifyDataSetChanged()
                }*/
            }else{
                DialogUtils.showErrorDialoge(this@SplashActivity,it.description)
            }
        })
    }

    fun setDeviceIMEI(){
        var myuniqueID: String?
        val myversion = Integer.valueOf(Build.VERSION.SDK)
        if (myversion < 23) {
            val manager =
                applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val info = manager.connectionInfo
            myuniqueID = info.macAddress
            if (myuniqueID == null) {
                val mngr =
                    getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                myuniqueID = mngr.deviceId
            }
        } else if (myversion > 23 && myversion < 29) {
            val mngr =
                getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            myuniqueID = mngr.deviceId
        } else {
            val androidId: String =
                Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
            myuniqueID = androidId
        }

        Constants.CURRENT_DEVICE_ID = myuniqueID.toString()
    }

    fun getPhoneIMEI(context: Context): String? {
        var deviceID = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var permissionResult =
                context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE)
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                permissionResult =
                    context.checkCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE")
            }
            val isPermissionGranted =
                permissionResult == PackageManager.PERMISSION_GRANTED
            if (!isPermissionGranted) {
                deviceID = getDeviceIDFromReflection(context).toString()
            } else {
                deviceID = getDeviceIDFromSystem(context).toString()
            }
        } else {
            deviceID = getDeviceIDFromSystem(context).toString()
        }

        return deviceID
    }

    fun getDeviceIDFromReflection(context: Context?): String? {
        var deviceID = ""
        try {
            val multiSimUtilsClazz =
                Class.forName("android.provider.MultiSIMUtils")
            val getDefaultMethod: Method =
                multiSimUtilsClazz.getMethod("getDefault", Context::class.java)
            val `object`: Any = getDefaultMethod.invoke(null, context)
            val method: Method =
                multiSimUtilsClazz.getMethod("getDeviceId", Int::class.javaPrimitiveType)
            deviceID = method.invoke(`object`, 0) as String
        } catch (e: Exception) {
        }
        return deviceID
    }

    @SuppressLint("MissingPermission")
    fun getDeviceIDFromSystem(context: Context): String? {
        val tm =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var deviceID: String? = null
        if (tm != null) {
            try {
                deviceID = tm.deviceId
            } catch (e: java.lang.Exception) {

            }
        }
        return deviceID
    }


}