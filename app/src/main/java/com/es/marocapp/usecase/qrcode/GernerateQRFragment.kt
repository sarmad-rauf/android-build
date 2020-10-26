package com.es.marocapp.usecase.qrcode

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.es.marocapp.R
import com.es.marocapp.databinding.FragmentGenerateQrBinding
import com.es.marocapp.locale.LanguageData
import com.es.marocapp.usecase.BaseFragment
import com.es.marocapp.usecase.MainActivity
import com.es.marocapp.utils.Constants
import com.es.marocapp.utils.Tools
import kotlinx.android.synthetic.main.fragment_generate_qr.*

class GernerateQRFragment : BaseFragment<FragmentGenerateQrBinding>(){

    override fun setLayout(): Int {
        return R.layout.fragment_generate_qr

    }

    override fun init(savedInstanceState: Bundle?) {
        var qrString= Tools.generateEMVcoString(Constants.CURRENT_USER_MSISDN,"")
        Log.d("QRString",qrString)
        imgResult.setImageBitmap(Tools.generateQR(qrString))
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

        setStrings()

        mDataBinding.inputAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int,count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,before: Int, count: Int) {
                var qrString= Tools.generateEMVcoString(Constants.CURRENT_USER_MSISDN,s.toString())
                Log.d("QRString",qrString)
                imgResult.setImageBitmap(Tools.generateQR(qrString))
            }
        })
    }

    private fun setStrings() {
        mDataBinding.tvGenerateQRTitle.text = LanguageData.getStringValue("GenerateQR")
        mDataBinding.tvDescription.text  = LanguageData.getStringValue("GenerateQRDescription")
        mDataBinding.inputAmount.hint  = LanguageData.getStringValue("Amount")
    }

}