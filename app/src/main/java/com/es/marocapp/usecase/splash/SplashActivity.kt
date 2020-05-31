package com.es.marocapp.usecase.splash


import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
            val telephonyManager =
                application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            Constants.CURRENT_DEVICE_ID = telephonyManager!!.deviceId
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
                    val telephonyManager =
                        application.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
                    Constants.CURRENT_DEVICE_ID = telephonyManager!!.deviceId
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

                startNewActivityAndClear(this@SplashActivity, LoginActivity::class.java)
            } else {
                Toast.makeText(this@SplashActivity, "API Failed", Toast.LENGTH_SHORT).show()
            }
        }

        val errorText = Observer<String> {
//            startNewActivityAndClear(this@SplashActivity, LoginActivity::class.java)
            Toast.makeText(this@SplashActivity, "Error", Toast.LENGTH_SHORT).show()

        }

        mActivityViewModel.mHandler.observe(this, resultObserver)
        mActivityViewModel.preLoginDataResponseListener.observe(this, preLoginDataObserver)
        mActivityViewModel.errorText.observe(this, errorText)
    }


}