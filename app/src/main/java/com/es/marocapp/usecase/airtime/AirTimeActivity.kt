package com.es.marocapp.usecase.airtime

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivityAirTimeBinding
import com.es.marocapp.databinding.ActivityCashServicesBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.cashservices.CashServicesViewModel
import com.es.marocapp.usecase.qrcode.ScanQRActivity
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity.Companion.KEY_SCANNED_DATA
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity.Companion.SCAN_QR
import com.es.marocapp.utils.Constants
import com.es.marocapp.widgets.MarocEditText
import com.es.marocapp.widgets.MarocMediumTextView
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.layout_activity_header.view.*
import kotlinx.android.synthetic.main.layout_simple_header.view.*
import java.util.regex.Pattern

class AirTimeActivity : BaseActivity<ActivityAirTimeBinding>() {

    lateinit var mActivityViewModel: AirTimeViewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    lateinit var navGraph: NavGraph

    var isQuickRechargeUseCase = false
    var quickRechargeAmount = ""

    lateinit var mInputField: MarocEditText
    lateinit var mInputFieldLayout: TextInputLayout
    lateinit var mInputHint: MarocMediumTextView

    private val CAMERA_REQUEST_CODE = 113

    override fun setLayout(): Int {
        return R.layout.activity_air_time
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(this@AirTimeActivity).get(AirTimeViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        isQuickRechargeUseCase = intent.getBooleanExtra("isQuickRechargeCase",false)
        if(isQuickRechargeUseCase){
            quickRechargeAmount = intent.getStringExtra("quickRechargeAmount")
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_air_time_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navGraph = navController.navInflater.inflate(R.navigation.air_time_nav_graph)

        setHeaderTitle(LanguageData.getStringValue("AirTime").toString())

        mDataBinding.root.activityHeaderBack.setOnClickListener {
            if (mActivityViewModel.popBackStackTo == -1) {
                this@AirTimeActivity.finish()
            } else {
                navController.popBackStack(mActivityViewModel.popBackStackTo, false)
            }
        }

        setCompanyIconToolbarVisibility(false)
        setFragmentToShow()
    }
    fun setFragmentToShow(){
        if(isQuickRechargeUseCase){
            mActivityViewModel.isQuickRechargeUseCase.set(true)
            mActivityViewModel.isRechargeFixeUseCase.set(false)
            mActivityViewModel.isRechargeMobileUseCase.set(false)
            mActivityViewModel.airTimeSelected.set(LanguageData.getStringValue("QuickRecharge"))
            mActivityViewModel.airTimeAmountSelected.set(quickRechargeAmount)
            navGraph.startDestination = R.id.airTimeConfirmationFragment
        }else{
            navGraph.startDestination = R.id.airTimeMainFragment
        }

        navController.setGraph(navGraph)
    }

    fun setHeaderTitle(title: String) {
        mDataBinding.headerAirTime.rootView.activityHeaderTitle.text = title
    }

    fun setCompanyIconToolbarVisibility(isVisible : Boolean){
        if(isVisible){
            mDataBinding.headerAirTime.rootView.headerCompanyIconContainer.visibility = View.VISIBLE
        }
        else{
            mDataBinding.headerAirTime.rootView.headerCompanyIconContainer.visibility = View.GONE
        }
    }

    fun setHeaderVisibility(isVisible: Boolean) {
        if (isVisible) {
            mDataBinding.headerAirTime.visibility = View.VISIBLE
        } else {
            mDataBinding.headerAirTime.visibility = View.GONE
        }
    }

    fun setVisibilityAndTextToImage(amount : String){
        mDataBinding.headerAirTime.rootView.img_company_icons.visibility = View.GONE
        mDataBinding.headerAirTime.rootView.first_letter_icons.visibility = View.VISIBLE

        mDataBinding.headerAirTime.rootView.first_letter_icons.text = amount
    }

    fun startQRScan(
        inputPhoneNumber: MarocEditText,
        inputLayoutPhoneNumber: TextInputLayout,
        inputPhoneNumberHint: MarocMediumTextView
    ) {
        mInputFieldLayout = inputLayoutPhoneNumber
        mInputField = inputPhoneNumber
        mInputHint = inputPhoneNumberHint

        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.d("CameraPermission", "Permission to access camera denied")
            makeRequestPermission()
        } else {
            startActivityForResult(Intent(this, ScanQRActivity::class.java),SCAN_QR)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == SCAN_QR) {
            val result = data
            val scannedString=result?.getStringExtra(KEY_SCANNED_DATA)
            if (result != null) {
                if (scannedString.isNullOrEmpty()) {
//                DialogUtils.showErrorDialoge(this@SendMoneyActivity, LanguageData.getStringValue("PleaseScanValidQRDot"))
                    mInputFieldLayout.isErrorEnabled = true
                    mInputFieldLayout.error = LanguageData.getStringValue("PleaseScanValidQRDot")
                    mInputFieldLayout.hint =
                        LanguageData.getStringValue("EnterReceiversMobileNumber")
                    mInputHint.visibility = View.GONE
                } else {
                    //var sResult = result.contents

                    verifyAndSetMsisdn(scannedString, false)
                }
            } else {
                // This is important, otherwise the result will not be passed to the fragment
                super.onActivityResult(requestCode, resultCode, data)
                mInputField.setText("")
//            DialogUtils.showErrorDialoge(this@SendMoneyActivity, LanguageData.getStringValue("PleaseScanValidQRDot"))
                mInputFieldLayout.isErrorEnabled = true
                mInputFieldLayout.error = LanguageData.getStringValue("PleaseScanValidQRDot")
                mInputFieldLayout.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
                mInputHint.visibility = View.GONE
            }
        }
    }

    private fun verifyAndSetMsisdn(sResult: String?, isFromPhonebook: Boolean) {
        if (isValidNumber(sResult!!)) {
            mInputFieldLayout.isErrorEnabled = false
            mInputFieldLayout.error = ""
            var msisdn = sResult
            if (msisdn.contains("212")) {
                msisdn = msisdn.substringAfter("212")
                msisdn = msisdn.substringAfter("+212")
                msisdn = msisdn.replace("-", "")
                msisdn = msisdn.trim()
                msisdn = "0$msisdn"
            }
            if (msisdn.contains("(")) {
                msisdn = msisdn.replace("(", "")
                msisdn = msisdn.replace(")", "")
                msisdn = msisdn.trim()
            }

            msisdn = msisdn.replace("-", "")
            msisdn = msisdn.replace(" ", "")
            msisdn = msisdn.trim()

            mInputFieldLayout.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
            mInputHint.visibility = View.GONE
            mInputField.setText(msisdn)
        } else {
            mInputField.setText("")
//                    DialogUtils.showErrorDialoge(this@SendMoneyActivity, LanguageData.getStringValue("PleaseScanValidQRDot"))
            mInputFieldLayout.isErrorEnabled = true
            if (isFromPhonebook) {
                mInputFieldLayout.error =
                    LanguageData.getStringValue("PleaseEnterValidMobileNumber")
                mInputFieldLayout.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
                mInputHint.visibility = View.GONE
            } else {
                mInputFieldLayout.error =
                    LanguageData.getStringValue("PleaseScanValidQRDot")
                mInputFieldLayout.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
                mInputHint.visibility = View.GONE
            }
        }
    }

    private fun isValidNumber(result: String): Boolean {
        var isNumberRegexMatches = false
        var msisdn = result
        if (msisdn.contains("212")) {
            msisdn = msisdn.substringAfter("212")
            msisdn = msisdn.substringAfter("+212")
            msisdn = msisdn.replace("-", "")
            msisdn = msisdn.trim()
            msisdn = "0$msisdn"
        }
        if (msisdn.contains("(")) {
            msisdn = msisdn.replace("(", "")
            msisdn = msisdn.replace(")", "")
            msisdn = msisdn.trim()
        }

        msisdn = msisdn.replace("-", "")
        msisdn = msisdn.replace(" ", "")
        msisdn = msisdn.trim()



        var msisdnLenght = msisdn.length
        isNumberRegexMatches =
            (msisdnLenght > 0 && msisdnLenght == Constants.APP_MSISDN_LENGTH.toInt() - 2 && Pattern.matches(
                Constants.APP_MSISDN_REGEX,
                msisdn
            ))
        return isNumberRegexMatches
    }

    fun makeRequestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.d("CameraPermission", "Permission to access camera denied")
                } else {
                    startActivityForResult(Intent(this, ScanQRActivity::class.java),SCAN_QR)
                }
            }
        }
    }
}
