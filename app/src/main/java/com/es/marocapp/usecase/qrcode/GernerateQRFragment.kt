package com.es.marocapp.usecase.qrcode

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentGenerateQrBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.network.ApiConstant
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.usecase.login.LoginActivity
import com.es.marocapp.usecase.payments.PaymentsViewModel
import com.es.marocapp.usecase.sendmoney.SendMoneyActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.DialogUtils
import com.es.marocapp.utils.Logger
import com.es.marocapp.utils.Tools
import kotlinx.android.synthetic.main.fragment_generate_qr.*

class GernerateQRFragment : BaseFragment<FragmentGenerateQrBinding>() {

    lateinit var mActivityViewModel: GenerateQRViewModel

    private var merchantCode = ""
    private lateinit var merchantName: String

    override fun setLayout(): Int {
        return R.layout.fragment_generate_qr

    }

    override fun init(savedInstanceState: Bundle?) {
        mActivityViewModel = ViewModelProvider(this).get(GenerateQRViewModel::class.java)
        mDataBinding.viewmodel = mActivityViewModel
        mDataBinding.imgBackButton.setOnClickListener {
            (activity as MainActivity).showTransactionsDetailsIndirectly = false
            (activity as MainActivity).navController.navigateUp()
        }

        (activity as MainActivity).setHomeToolbarVisibility(false)

        (activity as MainActivity).isDirectCallForTransaction = false
        (activity as MainActivity).isTransactionFragmentNotVisible = true
        (activity as MainActivity).showTransactionsDetailsIndirectly = true

        //----------for handling Backpress of activity----------
        (activity as MainActivity).isGenerateQRFragmentShowing = true
        (activity as MainActivity).isFaqsFragmentShowing = false
        (activity as MainActivity).isSideMenuShowing = false
        (activity as MainActivity).isTranactionDetailsFragmentShowing = false
        (activity as MainActivity).isHomeFragmentShowing = false
        (activity as MainActivity).isTransacitonFragmentShowing = false

        if (Constants.IS_MERCHANT_USER) {
            subscribeToObservers()
            mActivityViewModel.requestForAccountHolderAddtionalInformationApi(context)
        } else {
            var qrString = Tools.generateEMVcoString(Constants.CURRENT_USER_MSISDN, "")
            Logger.debugLog("QRString - Consumer", qrString)
            imgResult.setImageBitmap(Tools.generateQR(qrString))
        }

        setStrings()

        mDataBinding.inputAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var qrString = ""

                val qrstringg="000201010211269100325bb66a92d69c0ea742dd4f754590fa0a02011050100624hCeNuK39e/cWDDroomAv7w==0713+2126######565204431253035045802MA5910REGIS TEST6004SAFI8062003237b3a355b830b3bf0974d23608a6f162010110401005060100020602016304"
                    val generatedCRC = Tools.generateChecksumCRC16(qrstringg.toByteArray())
                    Logger.debugLog("abrar","crc=  ${Integer.toHexString(generatedCRC)}")
                    Logger.debugLog("QRString - Consumer", qrString)
                if (Constants.IS_MERCHANT_USER) {
                    qrString = Tools.generateMerchantEMVcoString(
                        Constants.CURRENT_USER_MSISDN,
                        s.toString(),
                        merchantCode,
                        merchantName
                    )
                    Logger.debugLog("QRString - Merchant", qrString)
                } else {
                    qrString =
                        Tools.generateEMVcoString(Constants.CURRENT_USER_MSISDN, s.toString())

                }
                imgResult.setImageBitmap(Tools.generateQR(qrString))
            }
        })
    }

    private fun subscribeToObservers() {
        mActivityViewModel.errorText.observe(this@GernerateQRFragment, Observer {
            DialogUtils.showErrorDialoge(activity as MainActivity, it)
        })

        mActivityViewModel.getAccountHolderAdditionalInfoResponseListner.observe(this@GernerateQRFragment,
            Observer {
                if (it.responseCode.equals(ApiConstant.API_SUCCESS)) {
                    Logger.debugLog("GenerateQRFragment", it.additionalinformation.toString())
                    if (!it.additionalinformation.isNullOrEmpty()) {
                        for (i in it.additionalinformation.indices) {
                            if (it.additionalinformation[i].name.contains("merchantid")) {
                                merchantCode = it.additionalinformation[i].value
                            }
                        }
                        merchantName = "${Constants.balanceInfoAndResponse?.firstname!!.toUpperCase()} ${Constants.balanceInfoAndResponse?.surname!!.toUpperCase()}"
                        Logger.debugLog("QRString - Merchant Code", merchantCode)
                        Logger.debugLog("QRString - Merchant Name", merchantName)
                        var qrString = Tools.generateMerchantEMVcoString(
                            Constants.CURRENT_USER_MSISDN,
                            "",
                            merchantCode,
                            merchantName
                        )
                        Logger.debugLog("QRString - Merchant", qrString)
                        imgResult.setImageBitmap(Tools.generateQR(qrString))
                    }
                } else {
                    DialogUtils.showErrorDialoge(activity as MainActivity, it.description)
                }
            }
        )
    }

    private fun setStrings() {
        mDataBinding.tvGenerateQRTitle.text = LanguageData.getStringValue("GenerateQR")
        mDataBinding.tvDescription.text = LanguageData.getStringValue("GenerateQRDescription")
        mDataBinding.tvDescriptionGenerated.text = LanguageData.getStringValue("QRDescription")
        mDataBinding.inputAmount.hint = LanguageData.getStringValue("Amount")
    }

}