package com.es.marocapp.usecase.sendmoney

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivitySendMoneyBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.widgets.MarocEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.layout_simple_header.view.*
import java.util.regex.Pattern

class SendMoneyActivity : BaseActivity<ActivitySendMoneyBinding>() {

    lateinit var mActivityViewModel: SendMoneyViewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    lateinit var mInputField : MarocEditText
    lateinit var mInputFieldLayout : TextInputLayout

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this@SendMoneyActivity).get(SendMoneyViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_send_money_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        mDataBinding.root.simpleHeaderBack.setOnClickListener {
            if (mActivityViewModel.popBackStackTo == -1) {
                this@SendMoneyActivity.finish()
            } else {
                navController.popBackStack(mActivityViewModel.popBackStackTo, false)
            }
        }

//        setHeaderTitle(LanguageData.getStringValue("SendMoney").toString())
    }

    override fun setLayout(): Int {
        return R.layout.activity_send_money
    }

    fun startQRScan(
        inputPhoneNumber: MarocEditText,
        inputLayoutPhoneNumber: TextInputLayout
    ) {
        mInputFieldLayout = inputLayoutPhoneNumber
        mInputField = inputPhoneNumber
        val integrator = IntentIntegrator(this@SendMoneyActivity)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
        integrator.setPrompt("")
        integrator.setOrientationLocked(false)
        integrator.setCameraId(0)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(false)
        integrator.initiateScan()
    }

    fun setHeaderTitle(title : String){
        mDataBinding.headerBillPayment.rootView.simpleHeaderTitle.text = title
    }

    fun setHeaderVisibility(isVisible: Boolean){
        if(isVisible){
            mDataBinding.headerBillPayment.visibility = View.VISIBLE
        }else{
            mDataBinding.headerBillPayment.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
//                DialogUtils.showErrorDialoge(this@SendMoneyActivity, LanguageData.getStringValue("PleaseScanValidQRDot"))
               mInputFieldLayout.isErrorEnabled= true
               mInputFieldLayout.error= LanguageData.getStringValue("PleaseScanValidQRDot")
            } else {
                var sResult = result.contents
                if(isValidQR(sResult)){
                    mInputFieldLayout.isErrorEnabled= false
                    mInputFieldLayout.error= ""
                    var msisdn = sResult
                    if(msisdn.contains("212")){
                        msisdn = msisdn.substringAfter("212")
                        msisdn = msisdn.substringAfter("+212")
                        msisdn = "0$msisdn"
                    }
                    mInputField.setText(msisdn)
                }else{
                    mInputField.setText("")
//                    DialogUtils.showErrorDialoge(this@SendMoneyActivity, LanguageData.getStringValue("PleaseScanValidQRDot"))
                    mInputFieldLayout.isErrorEnabled= true
                    mInputFieldLayout.error= LanguageData.getStringValue("PleaseScanValidQRDot")
                }
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data)
            mInputField.setText("")
//            DialogUtils.showErrorDialoge(this@SendMoneyActivity, LanguageData.getStringValue("PleaseScanValidQRDot"))
            mInputFieldLayout.isErrorEnabled= true
            mInputFieldLayout.error= LanguageData.getStringValue("PleaseScanValidQRDot")
        }
    }

    private fun isValidQR(result: String): Boolean {
        var isNumberRegexMatches = false
        var msisdn = result
        if(result.contains("212")){
            msisdn = msisdn.substringAfter("212")
            msisdn = msisdn.substringAfter("+212")
            msisdn = "0$msisdn"
        }
        var msisdnLenght = msisdn.length
        isNumberRegexMatches =
            (msisdnLenght > 0 && msisdnLenght == Constants.APP_MSISDN_LENGTH.toInt()-2 && Pattern.matches(Constants.APP_MSISDN_REGEX, msisdn))
        return isNumberRegexMatches
    }
}
