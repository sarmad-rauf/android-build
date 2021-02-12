package com.es.marocapp.usecase.sendmoney

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.es.marocapp.R
import com.es.marocapp.databinding.ActivitySendMoneyBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.security.EncryptionUtils
import com.es.marocapp.usecase.BaseActivity
import com.es.marocapp.usecase.qrcode.ScanQRActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.Logger
import com.es.marocapp.utils.Tools
import com.es.marocapp.widgets.MarocEditText
import com.es.marocapp.widgets.MarocMediumTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.layout_simple_header.view.*
import org.apache.commons.lang3.StringUtils
import java.util.regex.Pattern


class SendMoneyActivity : BaseActivity<ActivitySendMoneyBinding>() {

    lateinit var mActivityViewModel: SendMoneyViewModel

    lateinit var navController: NavController

    lateinit var navHostFragment: NavHostFragment

    lateinit var mInputField: MarocEditText
    lateinit var mInputFieldLayout: TextInputLayout
    lateinit var mInputHint: MarocMediumTextView
    private val CAMERA_REQUEST_CODE = 113
    val PICK_CONTACT = 10021

    companion object {
        val SCAN_QR = 1213
        val KEY_SCANNED_DATA = "key.scanned.string"
    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel =
            ViewModelProvider(this@SendMoneyActivity).get(SendMoneyViewModel::class.java)
        mDataBinding.apply {
            viewmodel = mActivityViewModel
        }

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_send_money_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        mDataBinding.root.simpleHeaderBack.setOnClickListener {
            if (mActivityViewModel.popBackStackTo == -1) {
                this@SendMoneyActivity.finish()
            } else {
                navController.popBackStack(mActivityViewModel.popBackStackTo, false)
            }
        }

//        setHeaderTitle(LanguageData.getStringValue("SendMoney").toString())
//        val decrptedNumber = EncryptionUtils.decryptStringAESCBC("u6WtONt3EufbU0DP3edx1A==")
//        Logger.debugLog("AESCBCNumber",decrptedNumber)
    }

    override fun setLayout(): Int {
        return R.layout.activity_send_money
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
            Logger.debugLog("CameraPermission", "Permission to access camera denied")
            makeRequestPermission()
        } else {
            startActivityForResult(Intent(this, ScanQRActivity::class.java), SCAN_QR)
        }


        /*val integrator = IntentIntegrator(this@SendMoneyActivity)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
        integrator.setPrompt("")
        integrator.setOrientationLocked(false)
        integrator.setCameraId(0)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(false)
        integrator.initiateScan()*/
    }

    fun openPhoneBook() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT)
    }

    fun setHeaderTitle(title: String) {
        mDataBinding.headerBillPayment.rootView.simpleHeaderTitle.text = title
    }

    fun setHeaderVisibility(isVisible: Boolean) {
        if (isVisible) {
            mDataBinding.headerBillPayment.visibility = View.VISIBLE
        } else {
            mDataBinding.headerBillPayment.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       Logger.debugLog("Abro","qr value ${data.toString()}")
        if (requestCode == PICK_CONTACT && resultCode === Activity.RESULT_OK) {
            val contactData = data!!.data
            val cursor: Cursor? = contentResolver.query(
                contactData!!,
                null,
                null,
                null,
                null
            )
            cursor?.moveToFirst()

            val number =
                cursor?.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            if (number == null || number.isNullOrEmpty()) {
                Logger.debugLog("Abro","number is null ${number}")
                mInputFieldLayout.isErrorEnabled = true
                mInputFieldLayout.error = LanguageData.getStringValue("PleaseEnterValidMobileNumber")
                mInputFieldLayout.hint = LanguageData.getStringValue("EnterMobileNumber")
                mInputHint.visibility = View.GONE
            } else {
                var sResult = number
                verifyAndSetMsisdn(sResult, true)
            }
        } else if (requestCode == SCAN_QR) {
            val result = data
            val scannedString = result?.getStringExtra(KEY_SCANNED_DATA)
            if (result != null) {
                Logger.debugLog("TestingQRString", scannedString!!)
                if (scannedString.isNullOrEmpty()) {
                    mInputFieldLayout.isErrorEnabled = true
                    mInputFieldLayout.error = LanguageData.getStringValue("PleaseScanValidQRDot")
                    mInputFieldLayout.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
                    mInputHint.visibility = View.GONE
                } else {
                    if (mActivityViewModel.isFundTransferUseCase.get()!!) {
                        if (Tools.validateConsumerEMVcoString(scannedString)) {
                            Logger.debugLog("TestingStringValidate", "Valid Consumer QR String")
                        } else {
                            mInputFieldLayout.isErrorEnabled = true
                            mInputFieldLayout.error = LanguageData.getStringValue("PleaseScanValidQRDot")
                            mInputFieldLayout.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
                            mInputHint.visibility = View.GONE

                            return
                        }
                    } else {
                        if (Tools.validateMerchantEMVcoString(scannedString)) {
                            Logger.debugLog("TestingStringValidate", "Valid Merchant QR String")

                            //crc should not be empty value and Mai should not be nill
                            if (scannedString.isNullOrEmpty() || Tools.extractMerchantNameFromEMVcoQR(scannedString).isNullOrEmpty()) {
                                mInputFieldLayout.isErrorEnabled = true
                                mInputFieldLayout.error = LanguageData.getStringValue("PleaseScanValidQRDot")
                                mInputFieldLayout.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
                                mInputHint.visibility = View.GONE
                            } else {
                                var merchantName = Tools.extractMerchantNameFromEMVcoQR(scannedString)
                                Logger.debugLog("TestingMerchantName", merchantName)
                                mActivityViewModel.merchantName = merchantName
                            }

                            if (scannedString.isNullOrEmpty() || Tools.extractMerchantCodeFromEMVcoQR(scannedString).isNullOrEmpty()) {
                                mInputFieldLayout.isErrorEnabled = true
                                mInputFieldLayout.error = LanguageData.getStringValue("PleaseScanValidQRDot")
                                mInputFieldLayout.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
                                mInputHint.visibility = View.GONE
                            } else {
                                var merchantCode = Tools.extractMerchantCodeFromEMVcoQR(scannedString)
                                Logger.debugLog("TestingMerchantCode", merchantCode)
                                mActivityViewModel.merchantCode = merchantCode
                            }
                        } else {
                            mInputFieldLayout.isErrorEnabled = true
                            mInputFieldLayout.error = LanguageData.getStringValue("PleaseScanValidQRDot")
                            mInputFieldLayout.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
                            mInputHint.visibility = View.GONE

                            return
                        }
                    }
                }

                if (scannedString.isNullOrEmpty() || Tools.extractNumberFromEMVcoQR(scannedString).isNullOrEmpty()) {
//                DialogUtils.showErrorDialoge(this@SendMoneyActivity, LanguageData.getStringValue("PleaseScanValidQRDot"))
                    mInputFieldLayout.isErrorEnabled = true
                    mInputFieldLayout.error = LanguageData.getStringValue("PleaseScanValidQRDot")
                    mInputFieldLayout.hint =
                        LanguageData.getStringValue("EnterReceiversMobileNumber")
                    mInputHint.visibility = View.GONE
                } else {
                    //var sResult = result.contents
                    Logger.debugLog("TestingPhone", Tools.extractNumberFromEMVcoQR(scannedString))
                    verifyAndSetMsisdn(Tools.extractNumberFromEMVcoQR(scannedString), false)
                }

                if (scannedString.isNullOrEmpty() || Tools.extractAmountFromEMVcoQR(scannedString).isNullOrEmpty()) {
                    mActivityViewModel.amountScannedFromQR = "0"
                    Logger.debugLog("TestingAmount", mActivityViewModel.amountScannedFromQR)
                } else {
                    var amount = Tools.extractAmountFromEMVcoQR(scannedString)
                    if (amount.equals("00000")) {
                        mActivityViewModel.amountScannedFromQR = "0"
                    } else {
//                        amount = amount.replaceFirst("^0+(?!$)", "")
                        var withoutStartingZeroAmount = StringUtils.stripStart(amount, "0")
                        mActivityViewModel.amountScannedFromQR = withoutStartingZeroAmount
                        Logger.debugLog("TestingAmountAfterRegex", withoutStartingZeroAmount)
                    }
                    Logger.debugLog("TestingAmount", mActivityViewModel.amountScannedFromQR)
                }

                if (scannedString.isNullOrEmpty() || Tools.extractPointOfInitiationFromEMVcoQR(scannedString).isNullOrEmpty()) {
                    mInputFieldLayout.isErrorEnabled = true
                    mInputFieldLayout.error = LanguageData.getStringValue("PleaseScanValidQRDot")
                    mInputFieldLayout.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
                    mInputHint.visibility = View.GONE
                } else {
                    var qrType = Tools.extractPointOfInitiationFromEMVcoQR(scannedString)
                    mActivityViewModel.qrType = qrType
                    Logger.debugLog("TestingQRType", qrType)
                }

                if (scannedString.isNullOrEmpty() || Tools.extractGloballyUniqueIdentifierFromEMVcoQR(scannedString).isNullOrEmpty()) {
                    mInputFieldLayout.isErrorEnabled = true
                    mInputFieldLayout.error = LanguageData.getStringValue("PleaseScanValidQRDot")
                    mInputFieldLayout.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
                    mInputHint.visibility = View.GONE
                } else {
                    var globallyUniqueIdentifier = Tools.extractGloballyUniqueIdentifierFromEMVcoQR(scannedString)
                    Logger.debugLog("TestingGloballyUniqueIdentifier", globallyUniqueIdentifier)
                }

                if (scannedString.isNullOrEmpty() || Tools.extractCRCFromEMVcoQR(scannedString).isNullOrEmpty()) {
                    mInputFieldLayout.isErrorEnabled = true
                    mInputFieldLayout.error = LanguageData.getStringValue("PleaseScanValidQRDot")
                    mInputFieldLayout.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
                    mInputHint.visibility = View.GONE
                } else {
                    var CRC = Tools.extractCRCFromEMVcoQR(scannedString)
                    Logger.debugLog("TestingCRC", CRC)
                }

                mActivityViewModel.qrValue = scannedString!!

            } else {
                // This is important, otherwise the result will not be passed to the fragment
                super.onActivityResult(requestCode, resultCode, data)
                mInputField.setText("")
//            DialogUtils.showErrorDialoge(this@SendMoneyActivity, LanguageData.getStringValue("PleaseScanValidQRDot"))
                mInputFieldLayout.isErrorEnabled = true
                mInputFieldLayout.error = LanguageData.getStringValue("PleaseScanValidQRDot")
                mInputFieldLayout.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
                mInputHint.visibility = View.GONE
                mActivityViewModel.amountScannedFromQR = "0"
                Logger.debugLog("TestingAmount", mActivityViewModel.amountScannedFromQR)
            }
        }
    }

    private fun verifyAndSetMsisdn(sResult: String?, isFromPhonebook: Boolean) {
        Logger.debugLog("Abro","qr value ${sResult}")
        if (isValidNumber(sResult!!)) {
            Logger.debugLog("Abro","is valid numb")
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
                Logger.debugLog("Abro","is from phonebook")
                mInputFieldLayout.error =
                    LanguageData.getStringValue("PleaseEnterValidMobileNumber")
                mInputFieldLayout.hint = LanguageData.getStringValue("EnterReceiversMobileNumber")
                mInputHint.visibility = View.GONE
            } else {
                Logger.debugLog("Abro","is from phonebbok else")
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

                    Logger.debugLog("CameraPermission", "Permission to access camera denied")
                } else {
                    startActivityForResult(Intent(this, ScanQRActivity::class.java), SCAN_QR)
                }
            }
        }
    }
}
